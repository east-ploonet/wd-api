package com.saltlux.aice_fe._baseline.baseVo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseVo {

	@JsonIgnore
	private SearchVo search = new SearchVo();

	@Data
    @NoArgsConstructor
	public static class SearchVo {

		private int     page     = 1;   // 현재 page 번호
		private int     pageSize = 10;  // 페이지당 출력 갯수
		private int     totalCnt = 0;   // 검색결과 총 count

		private int     startSeq = 0;   // 검색 요청할 시작 인덱스
		private int     limitCnt = 10;  // startSeq 부터 불러올 개수

		private String  searchColumn;
		private String  searchString;

//		private String  searchDateStart;
//		private String  searchDateEnd;
		private String  searchStatus;
		private String  startDate;
		private String  endDate;

		private String orderBy;
		private String orderType;
		
		private String staffName;

		
		public SearchVo(String searchStatus) {
		    this.searchStatus = searchStatus;
        }

		public SearchVo(int pageSize) {
		    this.pageSize = pageSize;
        }
	}
}
