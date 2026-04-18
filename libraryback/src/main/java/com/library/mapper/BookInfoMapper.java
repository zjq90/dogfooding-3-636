package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.BookInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 图书信息数据访问层
 */
@Mapper
public interface BookInfoMapper extends BaseMapper<BookInfo> {
    
    Page<BookInfo> selectBookPage(Page<BookInfo> page, 
                                   @Param("keyword") String keyword, 
                                   @Param("categoryId") Long categoryId);
    
    BookInfo selectBookDetail(@Param("id") Long id);
    
    int updateAvailableQuantity(@Param("id") Long id, @Param("delta") int delta, @Param("now") LocalDateTime now);
    
    Long selectBookCount();
    
    Long selectTotalBookQuantity();
    
    List<java.util.Map<String, Object>> selectCategoryStats();
}
