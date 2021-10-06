package com.qingyun.im.auth.mapper;

import com.qingyun.im.auth.vo.User;
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
 * @since 2021-09-23
 */
@Repository
public interface UserMapper extends BaseMapper<User> {
    /**
     * 根据ids批量查询用户名
     * @param list 一系列用户id，注意这里的变量名一定要用list
     * @return 用户名
     */
    @Select("<script>" +
                "SELECT username FROM `user` WHERE id IN " +
                    "<foreach collection='list' item='id' open='(' separator=',' close=')'>" +
                        "#{id}" +
                    "</foreach>" +
            "</script>")
    List<String> selectUsernamesByIds(List<Long> list);
}
