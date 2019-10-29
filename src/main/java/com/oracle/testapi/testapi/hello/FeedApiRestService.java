package com.oracle.testapi.testapi.hello;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.javafaker.Faker;

@Service
public class FeedApiRestService {

	private Faker faker = new Faker(new Locale("en-US"));
	private static final String LEAD = "Lead";
	private static final String ACTOR_TYPE = "Person";
	private static final String ACTOR_NAME= "Matt Hooper";
	private static final String ACTOR_ID="MHoope";
	private static final String ATTRIBUTE_NAME="Lead Name";
	private static final String ATTRIBUTE_DESCRIPTION="Description";
	private static final String ATTRIBUTE_TYPE="String";
	private static final String ATTRIBUTE_DUEDATE="Due date";	
	
	
	@Autowired
	private FeedApiDao feedApiDao;
	
	public void saveBulkFeeds(int count) {
		feedApiDao.saveBulkFeeds(getFeedList(count));
	}
	
	private List<FeedApi> getFeedList(int count){
		List<FeedApi> feedList = new ArrayList<FeedApi>();
		
		for(int i=0;i<count;i++) {
			FeedApi feed = new FeedApi();
			Object object = new Object();
			Target target = new Target();
			Actor actor = new Actor();
			List<Attribute> attributes = new ArrayList<Attribute>();
			String id = UUID.randomUUID().toString();
			String leadName = faker.name().fullName();
			//DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZ");
			
			//create Object data
			object.setId(id);
			object.setType(LEAD);
			feed.setObject(object);
			
			//create target data
			target.setId(id);
			target.setType(LEAD);
			feed.setTarget(target);
			
			//create Actor Data
			actor.setDisplayName(ACTOR_NAME);
			actor.setId(ACTOR_ID);
			actor.setType(ACTOR_TYPE);
			feed.setActor(actor);
			
			//set attribute data
			Attribute attribute1 = new Attribute();
			attribute1.setLabel(ATTRIBUTE_NAME);
			attribute1.setName(ATTRIBUTE_NAME);
			attribute1.setType(ATTRIBUTE_TYPE);
			attribute1.setValueString(leadName);
			
			Attribute attribute2 = new Attribute();
			attribute2.setLabel(ATTRIBUTE_DESCRIPTION);
			attribute2.setName(ATTRIBUTE_DESCRIPTION);
			attribute2.setType(ATTRIBUTE_TYPE);
			attribute2.setValueString(leadName.concat("is a good lead"));
			
			Attribute attribute3 = new Attribute();
			attribute3.setLabel(ATTRIBUTE_DUEDATE);
			attribute3.setName(ATTRIBUTE_DUEDATE);
			attribute3.setType("Date");
			attribute3.setValueString("2019-10-19T08:20:30+05:30");
			
			attributes.add(attribute1);
			attributes.add(attribute2);
			attributes.add(attribute3);
			
			feed.setAttributes(attributes);
			feed.setSummary(new StringBuilder(ACTOR_NAME)
					.append("created lead").
					append(leadName).toString());
			feed.setId(id);
			feed.setPublished("2019-10-19T08:20:30+05:30");
			feed.setType("CREATE");
			feedList.add(feed);
		}
		return feedList;
	}
}
