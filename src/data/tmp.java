package data;

abstract class ProductEntry {
	protected String title;
	protected String subtitle;
	protected int[] imageArray;
	protected String downloadLink;
	protected String description;

	public ProductEntry(String title, String subtitle, int[] imageArray, String downloadLink, String description) {
		this.title = title;
		this.subtitle = subtitle;
		this.imageArray = imageArray;
		this.downloadLink = downloadLink;
		this.description = description;
	}

	/*
	 * ~Getters and Setters~
	 */
	
	public class GenericEntry extends ProductEntry {
	    private static String newTitle = "This Title";
	    private static String newSubtitle = "That Subtitle";
	    private static int[] newImageArray = new int[]{R.drawable.picture1, R.drawable.picture2};
	    private static String newDownloadLink = "www.google.com";
	    private static String newDescription = "This is where my description would go, if I had one!";

	    public GenericEntry() {
	    	super(newTitle, newSubtitle, newImageArray, newDownloadLink,newDescription);
	    }
	}
}
