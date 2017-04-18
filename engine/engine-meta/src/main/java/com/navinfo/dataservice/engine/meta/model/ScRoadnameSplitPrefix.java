package com.navinfo.dataservice.engine.meta.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONObject;

/** 
* @ClassName:  ScRoadnameSplitPrefix 
* @author code generator
* @date 2017-03-23 07:07:41 
* @Description: TODO
*/
public class ScRoadnameSplitPrefix  {
	private Integer id ;
	private String wordCanSplit ;
	private String wordCannotSplit ;
	private Integer regionFlag ;
	private String langCode ;
	
	public ScRoadnameSplitPrefix (){
	}
	
	public ScRoadnameSplitPrefix (Integer id ,String wordCanSplit,String wordCannotSplit,Integer regionFlag,String langCode){
		this.id=id ;
		this.wordCanSplit=wordCanSplit ;
		this.wordCannotSplit=wordCannotSplit ;
		this.regionFlag=regionFlag ;
		this.langCode=langCode ;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getWordCanSplit() {
		return wordCanSplit;
	}
	public void setWordCanSplit(String wordCanSplit) {
		this.wordCanSplit = wordCanSplit;
	}
	public String getWordCannotSplit() {
		return wordCannotSplit;
	}
	public void setWordCannotSplit(String wordCannotSplit) {
		this.wordCannotSplit = wordCannotSplit;
	}
	public Integer getRegionFlag() {
		return regionFlag;
	}
	public void setRegionFlag(Integer regionFlag) {
		this.regionFlag = regionFlag;
	}
	public String getLangCode() {
		return langCode;
	}
	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ScRoadnameSplitPrefix [id=" + id +",wordCanSplit="+wordCanSplit+",wordCannotSplit="+wordCannotSplit+",regionFlag="+regionFlag+",langCode="+langCode+"]";
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((wordCanSplit == null) ? 0 : wordCanSplit.hashCode());
		result = prime * result + ((wordCannotSplit == null) ? 0 : wordCannotSplit.hashCode());
		result = prime * result + ((regionFlag == null) ? 0 : regionFlag.hashCode());
		result = prime * result + ((langCode == null) ? 0 : langCode.hashCode());
		return result;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ScRoadnameSplitPrefix other = (ScRoadnameSplitPrefix) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (wordCanSplit == null) {
			if (other.wordCanSplit != null)
				return false;
		} else if (!wordCanSplit.equals(other.wordCanSplit))
			return false;
		if (wordCannotSplit == null) {
			if (other.wordCannotSplit != null)
				return false;
		} else if (!wordCannotSplit.equals(other.wordCannotSplit))
			return false;
		if (regionFlag == null) {
			if (other.regionFlag != null)
				return false;
		} else if (!regionFlag.equals(other.regionFlag))
			return false;
		if (langCode == null) {
			if (other.langCode != null)
				return false;
		} else if (!langCode.equals(other.langCode))
			return false;
		return true;
	}
	
	
	
}
