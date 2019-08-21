package nl.uu.cs.ape.sat.utils;
/**
 * Class used for testing.
 * 
 * @author Vedran Kasalica
 *
 */
public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String x = "Max";
		String y = "Maxer";
		y = y.substring(0, 3);
		String z = "M";
		z += "ax";
		
		System.out.println(x.equals(y));
		System.out.println(z.equals(y));
		System.out.println(x.equals(z));
		
	}

}
