package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.BorrowRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 借阅记录数据访问层
 */
@Mapper
public interface BorrowRecordMapper extends BaseMapper<BorrowRecord> {
    
    Page<BorrowRecord> selectBorrowPage(Page<BorrowRecord> page,
                                         @Param("userId") Long userId,
                                         @Param("bookId") Long bookId,
                                         @Param("status") Integer status);
    
    BorrowRecord selectBorrowDetail(@Param("id") Long id);
    
    Integer selectBorrowingCount(@Param("userId") Long userId);
    
    Integer selectOverdueCount(@Param("today") LocalDate today);
    
    Integer selectUserOverdueCount(@Param("userId") Long userId, @Param("today") LocalDate today);
    
    List<Map<String, Object>> selectMonthlyBorrowStats(@Param("startDate") LocalDate startDate);
    
    List<Map<String, Object>> selectHotBooks();
    
    Long selectTotalBorrowCount();
}
