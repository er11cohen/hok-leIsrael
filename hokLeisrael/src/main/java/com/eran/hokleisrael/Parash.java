package com.eran.hokleisrael;

import android.os.Parcel;
import android.os.Parcelable;

public class Parash implements Parcelable {//more efficient than Serializable 
 
	 private String timeSaved = null;//for gallery file name
	
	 private String humashEn;
	 private String humashHe;
	 private String parshHe;
	 private String parshEn;
	 private int parshIndex;
	
	
	 private int day;
	 private int scrollY = 0;
	 private Boolean weekly = false;//?????? ?? ????? ??? ???? ???? ?????? ??????? ???? 
	 private Boolean isCurrentDay = false;//for daily Limud - hok leisrael ?? ???? ??? ?????? ????? ?????? ?? ???? ????
	 private String title;
		 
	 public Parash()
	 {
		 
	 }
	 public Parash(int parshIndex,String parshHe, String parshEn, String humashEn)
	 {
		this.parshIndex=parshIndex;
		this.parshEn=parshEn;
		this.parshHe=parshHe;
		this.humashEn=humashEn;
	 }
	
	
	public String getHumashEn() {
		return humashEn;
	}
	public void setHumashEn(String humashEn) {
		this.humashEn = humashEn;
	}

	public String getHumashHe() {
		return humashHe;
	}
	public void setHumashHe(String humashHe) {
		this.humashHe = humashHe;
	}
	
	
	public String getParshHe() {
		return parshHe;
	}
	
	public void setParshHe(String parshHe) {
		this.parshHe = parshHe;
	}
	
	public String getParshEn() {
		return parshEn;
	}
	
	public void setParshEn(String parshEn) {
		this.parshEn = parshEn;
	}
	
	public int getParshIndex() {
		return parshIndex;
	}
	public void setParshIndex(int parshIndex) {
		this.parshIndex = parshIndex;
	}
	
	
	 
	
	
	 public Parash(String parshHe, String parshEn, String humashEn,
			 int day, int scrollY,Boolean weekly,Boolean isCurrentDay, String title)
	 {
		this.parshEn=parshEn;
		this.parshHe=parshHe;
		this.humashEn=humashEn;
		
		this.day=day;
		this.scrollY=scrollY;
		this.weekly=weekly;
		this.isCurrentDay=isCurrentDay;
		this.title=title;
	 }
	 
	    public int getDay() {
			return day;
		}
		public void setDay(int day) {
			this.day = day;
		}
	
		 public int getScrollY() {
				return scrollY;
			}
			
		 public void setScrollY(int scrollY) {
				this.scrollY = scrollY;
			}


		public Boolean getWeekly() {
			return weekly;
		}


		public void setWeekly(Boolean weekly) {
			this.weekly = weekly;
		}


		public Boolean getIsCurrentDay() {
			return isCurrentDay;
		}


		public void setIsCurrentDay(Boolean isCurrentDay) {
			this.isCurrentDay = isCurrentDay;
		}


		public String getTitle() {
			return title;
		}


		public void setTitle(String title) {
			this.title = title;
		}

		public String getTimeSaved() {
			return timeSaved;
		}


		public void setTimeSaved(String timeSaved) {
			this.timeSaved = timeSaved;
		}

		@Override
		public String toString() {
		/*	return "Parash [humashEn=" + humashEn + ", parshHe=" + parshHe
					+ ", parshEn=" + parshEn + ", day=" + day + ", scrollY="
					+ scrollY + ", weekly=" + weekly + ", isCurrentDay="
					+ isCurrentDay + ", title=" + title + "]";
					*/
			return this.title;
		}
		
		public String getKey() {
			return humashEn + "_" + parshEn + "_" + day;
		}

    protected Parash(Parcel in) {
        timeSaved = in.readString();
        humashEn = in.readString();
        humashHe = in.readString();
        parshHe = in.readString();
        parshEn = in.readString();
        parshIndex = in.readInt();
        day = in.readInt();
        scrollY = in.readInt();
        byte weeklyVal = in.readByte();
        weekly = weeklyVal == 0x02 ? null : weeklyVal != 0x00;
        byte isCurrentDayVal = in.readByte();
        isCurrentDay = isCurrentDayVal == 0x02 ? null : isCurrentDayVal != 0x00;
        title = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(timeSaved);
        dest.writeString(humashEn);
        dest.writeString(humashHe);
        dest.writeString(parshHe);
        dest.writeString(parshEn);
        dest.writeInt(parshIndex);
        dest.writeInt(day);
        dest.writeInt(scrollY);
        if (weekly == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (weekly ? 0x01 : 0x00));
        }
        if (isCurrentDay == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (isCurrentDay ? 0x01 : 0x00));
        }
        dest.writeString(title);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Parash> CREATOR = new Parcelable.Creator<Parash>() {
        @Override
        public Parash createFromParcel(Parcel in) {
            return new Parash(in);
        }

        @Override
        public Parash[] newArray(int size) {
            return new Parash[size];
        }
    };
}