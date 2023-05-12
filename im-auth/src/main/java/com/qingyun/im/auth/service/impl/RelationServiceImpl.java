package com.qingyun.im.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qingyun.im.auth.service.UserService;
import com.qingyun.im.auth.vo.Relation;
import com.qingyun.im.auth.mapper.RelationMapper;
import com.qingyun.im.auth.service.RelationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qingyun.im.auth.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 張青云
 * @since 2022-12-05
 */
@Service
public class RelationServiceImpl extends ServiceImpl<RelationMapper, Relation> implements RelationService {

    @Autowired
    private UserService userService;

    @Autowired
    private RelationMapper relationMapper;


    @Override
    public boolean isFriend(String username1, String username2) {
        //  查询id
        Long id1 = userService.selectIdByUsername(username1);
        Long id2 = userService.selectIdByUsername(username2);

        Relation relation = selectOneByTwoId(id1, id2);
        return relation != null && relation.getStatus() == 0;
    }

    @Override
    public boolean askFriend(String username1, String username2) {
        //  查询id
        Long id1 = userService.selectIdByUsername(username1);
        Long id2 = userService.selectIdByUsername(username2);

        //  查询是否已有记录
        Relation historyRecord = selectOneByTwoId(id1, id2);
        if (historyRecord != null) {
            //  已建立好友关系
            if (historyRecord.getStatus() == 0) {
                return false;
            }
            //  已经发送过请求了
            if ((historyRecord.getUserId1().equals(id2) && historyRecord.getStatus() == 1) ||
                    (historyRecord.getUserId2().equals(id2) && historyRecord.getStatus() == 2)) {
                return true;
            }
            //  对方发送过好友请求了,则变为确认好友关系
            historyRecord.setStatus(0);
            relationMapper.updateById(historyRecord);
            return true;
        }


        Relation relation;
        if (id1 <= id2) {
            //  id1发送请求,等待id2接受,故status为2
            relation = new Relation(id1, id2, 2);
        } else {
            //  等待id2确认请求
            relation = new Relation(id2, id1, 1);
        }

        return relationMapper.insert(relation) == 1;
    }

    @Override
    public boolean ackFriend(String username1, String username2) {
        //  查询id
        Long id1 = userService.selectIdByUsername(username1);
        Long id2 = userService.selectIdByUsername(username2);

        //  查询是否有user2发给user1的好友请求
        Relation historyRecord = selectOneByTwoId(id1, id2);
        if (historyRecord != null) {
            if ((historyRecord.getUserId1().equals(id1) && historyRecord.getStatus() == 1) ||
                    (historyRecord.getUserId2().equals(id1) && historyRecord.getStatus() == 2)) {
                historyRecord.setStatus(0);
                relationMapper.updateById(historyRecord);
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> getFriendAsk(String username) {
        //  查询用户id
        Long userId = userService.selectIdByUsername(username);
        if (userId == null) {
            return null;
        }
        List<Relation> friendAsk = relationMapper.getFriendAsk(userId);
        if (friendAsk == null || friendAsk.size() == 0) {
            return null;
        }
        //  获取发起请求的用户id
        List<Long> ids = new ArrayList<>(friendAsk.size());
        for(Relation one: friendAsk) {
            if (one.getUserId1().equals(userId)) {
                ids.add(one.getUserId2());
            } else {
                ids.add(one.getUserId1());
            }
        }
        //  查询这些id对应的username
        return userService.selectUsernamesByIds(ids);
    }

    @Override
    public List<String> getFriendList(String username) {
        //  查询用户id
        Long userId = userService.selectIdByUsername(username);
        if (userId == null) {
            return null;
        }
        List<Relation> friendList = relationMapper.getFriendList(userId);
        if (friendList == null || friendList.size() == 0) {
            return null;
        }
        //  收集好友的id
        List<Long> ids = new ArrayList<>(friendList.size());
        for (Relation one: friendList) {
            if (one.getUserId1().equals(userId)) {
                ids.add(one.getUserId2());
            } else {
                ids.add(one.getUserId1());
            }
        }
        //  查询这些id对应的username
        return userService.selectUsernamesByIds(ids);
    }

    /**
     * 根据两个用户的id来查询两者之间是否存在记录
     */
    private Relation selectOneByTwoId(Long id1, Long id2) {
        QueryWrapper<Relation> wrapper = new QueryWrapper<>();
        if (id1 <= id2) {
            wrapper.eq("user_id1", id1);
            wrapper.eq("user_id2", id2);
        } else {
            wrapper.eq("user_id1", id2);
            wrapper.eq("user_id2", id1);
        }
        return relationMapper.selectOne(wrapper);
    }
}
