package com.qingyun.im.auth.mapper;

import com.qingyun.im.auth.vo.Relation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 張青云
 * @since 2021-10-05
 */
@Repository
public interface RelationMapper extends BaseMapper<Relation> {

    /**
     * 查询指定用户的好友申请列表
     * @param userId 指定的用户
     * @return 全部好友申请
     */
    @Select("select * from relation where (`user_id1`=#{userId} and `status`=1) or " +
            "(`user_id2`=#{userId} AND `status`=2)")
    List<Relation> getFriendAsk(Long userId);

    /**
     * 查询指定用户的好友列表
     * @param userId 指定的用户
     * @return 全部的好友关系
     */
    @Select("SELECT * FROM `relation` WHERE (`user_id1`=#{userId} OR `user_id2`=#{userId}) AND `status`=0")
    List<Relation> getFriendList(Long userId);
}
