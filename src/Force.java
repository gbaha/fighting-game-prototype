public class Force
{
	String type;
	int direction;	//direction(0,1,2,3)
	double magnitude, decay;
	
	public Force(String t, int d1, double m, double d2)
	{
		type = t;
		direction = d1;
		magnitude = m;
		decay = d2;
	}
}