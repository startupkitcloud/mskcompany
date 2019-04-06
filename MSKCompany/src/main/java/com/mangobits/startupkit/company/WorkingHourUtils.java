package com.mangobits.startupkit.company;

import java.text.SimpleDateFormat;
import java.util.*;

public class WorkingHourUtils {

	
	public static String businessHourDesc(List<WorkingHour> hours) throws Exception{
		
		StringBuilder desc = new StringBuilder();

		if(hours != null){

			SimpleDateFormat df = new SimpleDateFormat("EEE", new Locale("pt", "BR"));
			Map<String, List<String>> dates = new LinkedHashMap<>();

			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

			for(int i=0; i<7; i++){

				int weekDay = cal.get(Calendar.DAY_OF_WEEK);

				WorkingHour wh = hours.stream()
						.filter(p -> p.getWeekDay().equals(weekDay))
						.findFirst()
						.orElse(null);

				if(wh != null){

					String time = wh.getHourBegin() + " - " + wh.getHourEnd();

					if(!dates.containsKey(time)){
						dates.put(time, new ArrayList<>());
					}

					dates.get(time).add(df.format(cal.getTime()));
				}

				cal.add(Calendar.DAY_OF_WEEK, 1);
			}

			int count = 0;

			for(String hour : dates.keySet()){
				List<String> days = dates.get(hour);

				if(count != 0){
					if((count + 1) < dates.size()){
						desc.append(", ");
					}
					else{
						desc.append(" e ");
					}
				}

				if(days.size() == 1){
					desc.append(days.get(0));
				}
				else if(days.size() == 2){
					desc.append(days.get(0));
					desc.append(" à ");
					desc.append(days.get(1));
				}
				else{
					desc.append(days.get(0));
					desc.append(" - ");
					desc.append(days.get(days.size() -1));
				}


				desc.append(" ");
				desc.append(hour);

				count++;
			}
		}
		
		return desc.toString();
	}
}
