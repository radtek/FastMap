package com.navinfo.dataservice.engine.statics.writer;

/**
 * 统计结果写入类，一般默认使用DefaultWriter
 * @author zhangxiaoyi
 *
 */
public class WriterFactory {
	//子任务统计job
	private static final String subtask_job = "subtaskStat";
	//day_planjob
	private static final String day_plan_job = "dayPlanStat";
	//任务统计job
	private static final String quick_task_job = "quickTaskStat";
	private static final String medium_task_job = "mediumTaskStat";
	//项目统计job
	private static final String quick_program_job = "quickProgramStat";
	private static final String medium_program_job = "mediumProgramStat";
	//城市统计job
	private static final String city_job = "cityJob";
	//快线统计job
	private static final String quick_job = "quickMonitorStat";
	//中线统计job
	private static final String medium_job = "cityJob";
	//日出品统计job
	private static final String day_produce_job = "dayProduceStat";
	//大屏统计Job
	private static final String product_monitor_job = "productMonitorStat";
	
	public static DefaultWriter createWriter(String jobType){
		if(subtask_job.equals(jobType)){
			return new SubtaskWriter();
		}else if(day_plan_job.equals(jobType)){
			return new DayPlanWriter();
		}else if(quick_task_job.equals(jobType)){
			return new QuickTaskWriter();
		}else if(medium_task_job.equals(jobType)){
			return new MediumMonitorWriter();
		}else if(quick_program_job.equals(jobType)){
			return new QuickProgramWriter();
		}else if(medium_program_job.equals(jobType)){
			return new MediumMonitorWriter();
		}else if(city_job.equals(jobType)){
			return new CityWriter();
		}else if(quick_job.equals(jobType)){
			return new QuickMonitorWriter();
		}else if(medium_job.equals(jobType)){
			return new MediumMonitorWriter();
		}else if(day_produce_job.equals(jobType)){
			return new DayProduceWriter();
		}else if(product_monitor_job.equals(jobType)){
			return new ProductMonitorWriter();
		}else{
			return new DefaultWriter();		
		}
	}
}
