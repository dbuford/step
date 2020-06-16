// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    
    //return empty list if meeting duration is longer than a day
    if(request.getDuration()>TimeRange.WHOLE_DAY.duration()){
        return Arrays.asList();
    }
    //return full day available if there are no meeting attendees, including optional
    if(request.getAttendees().isEmpty()&& request.getOptionalAttendees().isEmpty()){
        return Arrays.asList(TimeRange.WHOLE_DAY);
    }
    //return full day available if there are no conflicts
    if(events.isEmpty()){
        return Arrays.asList(TimeRange.WHOLE_DAY);
    }

    //for each event, create two TimeRange objects that split the day into two, before and after meeting
    ArrayList<TimeRange> list=new ArrayList<TimeRange>();

    if(!request.getAttendees().isEmpty()){
        CreateTimeOptions(list,events,request.getAttendees());
    }

    //creates timeranges based on optional attendees events if no mandatory attendees
    else{
        CreateTimeOptions(list,events,request.getOptionalAttendees());
    }
    

    //return full day if no attendees in the meeting request have an event
      if(list.size()==0){
        return Arrays.asList(TimeRange.WHOLE_DAY);
      }
   
    //create new list that considers overlapping conflicts
    ArrayList<TimeRange> finalList=new ArrayList<TimeRange>();

    Collections.sort(list,TimeRange.ORDER_BY_START);

    for(TimeRange timerange:list){
        boolean overlap=Overlap(timerange, events, finalList, request);
        if(overlap==false&& timerange.duration()>=request.getDuration()&& finalList.contains(timerange)==false){
            finalList.add(timerange);
        }
    }
    return finalList;
  }
  /*
  helper function that determines whether any scheduled timerange overlaps with an event
  returns true if current timerange overlaps with event, otherwise false
  */
  public boolean Overlap(TimeRange timerange, Collection<Event> events, ArrayList<TimeRange> list, MeetingRequest request){
      boolean overlap=false;

      for(Event event:events){
          //checks to see if all day event is from optional attendee. If so, does not alter overlapping timerange
          if(request.getOptionalAttendees().contains(String.join(" ",event.getAttendees()))&& event.getWhen().duration()>=TimeRange.WHOLE_DAY.duration()){
            continue;
          }
          else{
            if(timerange.overlaps(event.getWhen())){
            //checks to see if timerange ends after event. If so, alters timerange start to the end of the event
            //to account for overlap
              if(timerange.end()> event.getWhen().end()){
                  //if overlapping event is from optional attendee and altered timerange will have smaller
                  //duration than requested time, do not alter timerange
                  if(request.getOptionalAttendees().contains(String.join(" ",event.getAttendees()))&&timerange.end()-event.getWhen().end()<request.getDuration()){
                    continue;
                }
                TimeRange updatedTimeRange=TimeRange.fromStartEnd(event.getWhen().end(),timerange.end(),false);
                
                if(Overlap(updatedTimeRange,events,list,request)==false&& updatedTimeRange.duration()>=request.getDuration()&& list.contains(updatedTimeRange)==false){
                    list.add(updatedTimeRange);   
                }
              }
            overlap=true;
          }
          }    
      }
      return overlap;
  }
  /*
  helper function that creates two new TimeRanges, before and after event and adds them to list 
  */
  public void CreateTimeOptions(ArrayList<TimeRange> list,Collection<Event> events,Collection<String> attendees){
    for(Event event:events){
      if(attendees.contains(String.join(" ",event.getAttendees()))){
            list.add(TimeRange.fromStartEnd(TimeRange.START_OF_DAY, event.getWhen().start(),false));
            list.add(TimeRange.fromStartEnd(event.getWhen().end(),TimeRange.END_OF_DAY,true));
        }
    }
  }

}

