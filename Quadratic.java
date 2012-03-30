/*
 * Mikhail Chliakhovski
 * 9730117
 * Albert Nguyen
 * 9242112
 * 
 * COMP 465
 * Assignment 4
 * 
 * A Quadratic Equation Solver. Real and complex roots are supported, but not complex coefficients. Loss of precision due to round-off
 * error and other conditions is detected where possible and computation is aborted. A mixed approach is used in place of the Quadratic Formula to avoid
 * subtractive cancellation. Newton's Method is used to compute the square root.
 */

import java.text.DecimalFormat;
import java.util.Scanner;

public class Quadratic {

	private static final double ERROR = 0.00000001; // acceptable error for Newton's Method
	
	/*
	 * Solves the quadratic equation and outputs roots to the screen. Throws an exception is precision is lost during calculation.
	 */
	public static void solveQuadratic(double a, double b, double c) throws NotEnoughPrecisionException {
		
		double sqrt, real, q;
		double discriminant = b*b - 4*a*c;
		String imaginary, output, x1, x2;
		
		// check for overflow and b^2 >> 4ac
		if (Double.isNaN(discriminant) || discriminant == b*b) {
			throw new NotEnoughPrecisionException();
		}
		
		if (discriminant < 0) { // complex roots
			sqrt = sqrtByNewton(-1*discriminant);
			real = (-1*b) / (2*a);
			imaginary = formatDouble(sqrt/(2*a));
			// don't print redundant zeros and signs
			output = "x1 = ";
			output += (real != 0) ? real + " + " : "";
			output += (!imaginary.equals("1")) ? imaginary : "";
			output += "i\nx2 = ";
			output += (real != 0) ? real + " - " : "-";
			output += (!imaginary.equals("1")) ? imaginary : "";
			output += "i";
			System.out.println(output);
		} else { // real roots
			sqrt = sqrtByNewton(discriminant);
			// mixed approach to avoid subtractive cancellation
			q = (-0.5) * (b + sign(b)*sqrt);
			x1 = formatDouble(q/a);
			x2 = formatDouble(c/q);
			// don't print the same root twice
			output = "x1 = " + x1;
			output += (!x1.equals(x2)) ? "\nx2 = " + x2 : "";
			System.out.println(output);
		}	
		
	}
	
	/*
	 * Extracts the sign of a double value.
	 */
	private static int sign(double b) {
		return (b > 0) ? 1 : -1;
	}
	
	/*
	 * Computes the square root of a number using Newton's Method. Returns when the error threshold has been reached.
	 */
	private static double sqrtByNewton(double value) {
		
		// square root of zero is zero
		if (value == 0) return 0;
		
		double result, previous;
		previous = (1 + value)/2;
		
		// iterate until error threshold is reached
		while(true) {
			result = (previous + value/previous) / 2;
			if (previous - result < ERROR)
				break;
			previous = result;
		}
		
		return result;
		
	}
	
	/* 
	 * Checks whether a double value actually represents a string, and formats accordingly.
	 */
	private static String formatDouble(double value) {
		
		// check if value is actually an integer
		if (Math.floor(value) == value) {
			Integer intValue = (int)value;
			return intValue.toString();
		} else {
			Double doubleValue = value;
			return doubleValue.toString();
		}
		
	}
	
	/*
	 * Validates the input by converting to type double and inspecting for overflow. Throws an exception if overflow occurred.
	 */
	public static double validateInput(String input) throws NotEnoughPrecisionException {
		
		// parse the input
		Double value = Double.parseDouble(input);
		
		// format value to decimal of (almost) arbitrary length
		DecimalFormat decimal = new DecimalFormat("###################################################################################################0.0" +
				"###################################################################################################");
		
		// append .0 when input is integer
		String formatted = input;
		if (input.indexOf('.') == -1) formatted += ".0";
		
		// if new value is not equal to original, overflow has occurred
		if (!decimal.format(value).equals(formatted) && !value.toString().equals(input)) // toString to validate e-notation
			throw new NotEnoughPrecisionException();
		
		return value;
		
	}
	
	public static void main(String[] args) {	
		
		double a, b, c;
		a = b = c = 0; // to keep the compiler happy
		
		String prompt;
		
		// welcome message
		System.out.println("Welcome to Quadratic Equation Solver.\n" +
				"A quadratic equation can be written in the form ax^2 + bx + c = 0, where x is an unknown, a, b, and c are constants, and a is not zero.\n" +
				"Given values for a, b, and c, this program will produce the two roots of the equation. Both real and complex roots are supported, but not complex coefficients.\n" +
				"Press Ctrl+C to quit at any time.");
		
		Scanner sc = new Scanner(System.in);
		
		// repeat until the user quits
		while (true) {
		
			// collect input from user
			try {
				System.out.println("Enter a value for 'a':");
				a = Quadratic.validateInput(sc.next()); // validate before storing
				// make sure a is not zero
				if (a == 0) {
					System.out.println("'a' cannot be zero!");
					continue;
				}
				System.out.println("Enter a value for 'b':");
				b = Quadratic.validateInput(sc.next());
				System.out.println("Enter a value for 'c':");
				c = Quadratic.validateInput(sc.next());
			} catch (NotEnoughPrecisionException e) {
				System.out.println("The value you entered is too large or too small! Please enter a value between " + Double.MIN_VALUE +
						" and " + Double.MAX_VALUE + ".");
				continue;
			} catch (NumberFormatException e) {
				System.out.println("The value you entered is not allowed! Please enter a number. E.g. 4, 0.3, -12");
				continue;
			}
		      
			// solve equation
			try {
				Quadratic.solveQuadratic(a, b, c);
			} catch (NotEnoughPrecisionException e) {
				System.out.println("Failed to find an accurate solution! This can happen when the values are too" +
						" big, or b^2 is much bigger than 4ac. Try using smaller values.");
			}
			
			// prompt user
			System.out.println("Would you like to try again? [y/n]");
			prompt = sc.next();
			
			// quit if no
			if (!prompt.equals("y"))
				break;
		
		}
		
		// goodbye
		System.out.println("Thank you for using Quadratic Equation Solver!");
		
	}

}
