package cn.henry.study.mapper;

import cn.henry.study.entity.GeneratorQuartzJob;

public interface GeneratorQuartzJobMapper {
    int deleteByPrimaryKey(Long id);

    int insert(GeneratorQuartzJob record);

    int insertSelective(GeneratorQuartzJob record);

    GeneratorQuartzJob selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(GeneratorQuartzJob record);

    int updateByPrimaryKey(GeneratorQuartzJob record);
}