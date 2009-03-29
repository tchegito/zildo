package zildo.monde.dialog;
// DialogTopic.cpp: implementation of the DialogTopic class.
//
//////////////////////////////////////////////////////////////////////


public class DialogTopic {
	

	private int topicId;
	private String topicName;
	private boolean accessible;

	//////////////////////////////////////////////////////////////////////
// Construction/Destruction
//////////////////////////////////////////////////////////////////////

	public DialogTopic()
	{
		this.topicId=0;
		this.topicName="";
		this.accessible=false;
	}
	
	public DialogTopic(int topicId, String topicName)
	{
		this.topicId=topicId;
		this.topicName=topicName;
		// Default : not accessible for player
		this.accessible=false;
	}

	public int getTopicId() {
		return topicId;
	}

	public void setTopicId(int topicId) {
		this.topicId = topicId;
	}

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	public boolean isAccessible() {
		return accessible;
	}

	public void setAccessible(boolean accessible) {
		this.accessible = accessible;
	}
}