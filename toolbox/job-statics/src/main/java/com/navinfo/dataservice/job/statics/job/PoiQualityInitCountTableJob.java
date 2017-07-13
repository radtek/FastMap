package com.navinfo.dataservice.job.statics.job;

import com.navinfo.dataservice.api.job.model.JobInfo;
import com.navinfo.dataservice.control.row.quality.PoiQuality;
import com.navinfo.dataservice.job.statics.AbstractStatJob;
import com.navinfo.dataservice.jobframework.exception.JobException;

public class PoiQualityInitCountTableJob extends AbstractStatJob {
	
	public PoiQualityInitCountTableJob(JobInfo jobInfo) {
		super(jobInfo);
	}

	@Override
	public String stat() throws JobException {
		PoiQualityInitCountTableJobRequest req = (PoiQualityInitCountTableJobRequest) this.request;
		try {
			log.info("开始初始化poi_count_table表----");
			PoiQuality poiQuality = new PoiQuality();
			poiQuality.initQualityData();
			log.info("初始化poi_count_table表完成----");
			return "初始化成功";
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new JobException(e.getMessage(), e);
		}
	}

	
}