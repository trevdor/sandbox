import com.google.common.base.Joiner;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Converts a positive, numeric dollar value to the corresponding English-language name.
 * Example: 2523.04 becomes "Two thousand five hundred twenty-three and 04/100 dollars"
 */
public class DollarsToWords {

	// All the English number words 
	static String[][] zeroTo19 = { 
		{"zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine"}, 
		{"ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eighteen", "nineteen"}
	};
	static String[] tens = { "ten", "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety" };
	static String[] largeOnes = { "", "thousand", "million", "billion", "trillion", "quadrillion", "quintillion" };
	
	public static void main(String[] args) {
		if (args.length != 1)
		{
			System.out.println("One and only one amount allowed.");
			System.exit(1);
		}
		
		String amount = args[0];	
		validateAmountString(amount);
		
		System.out.println(amount + " => " + translate(amount));
	}
	
	private static String translate(String amount)
	{
		String cents = "00";
		String dollars;
		
		// Tidy amount string to a string of digits, 
		// saving sign and cents portion for later
		String tidyAmount = amount.replaceAll("[\\$,]", "");
		
		if (Pattern.matches("\\d+\\.\\d{2}", tidyAmount))
		{
			String[] dollarsAndCents = tidyAmount.split("\\.");
			dollars = dollarsAndCents[0];
			cents = dollarsAndCents[1];
		} 
		else 
		{
			dollars = tidyAmount;
		}
		
		ArrayList englishAmountParts = new ArrayList();
		int length = dollars.length();
		for (int i=0; i < length; i++)
		{
			int placeValue = length - i - 1; // the power of 10 at this position
			int digit = Integer.parseInt(dollars.substring(i,i+1)); // digit at i
			
			if (placeValue % 3 == 0) // a relative ones place
			{
				englishAmountParts.add(zeroTo19[0][digit]);
				if ((placeValue/3) < largeOnes.length-1)
				{
					englishAmountParts.add(largeOnes[placeValue/3]);
				}
				else 
				{
 					System.err.println("The amount entered exceeds the supported maximum.");
					System.exit(1);
				}
			}
			else if (placeValue % 3 == 1) // we're in a relative tens place
			{
				int onesValue = Integer.parseInt(dollars.substring(i+1,i+2));
				
				// the number's name
				if (digit == 1)
					englishAmountParts.add(zeroTo19[digit][onesValue]); 
				else
					englishAmountParts.add(tens[digit-1] + "-" + zeroTo19[0][onesValue]);
				
				// the number's label
				if (placeValue != 1)
					englishAmountParts.add(largeOnes[(placeValue-1)/3]);
				
				i++;  // we just dealt with the imminent ones place, so skip it
			}
			else if (placeValue % 3 == 2) // relative hundreds place
			{
				englishAmountParts.add(zeroTo19[0][digit]);
				englishAmountParts.add("hundred");
			}
		}
		
		String dollarsInEnglish = Joiner.on(" ").join(englishAmountParts);
		String centsString = (cents.equals("00")) ? "" : " and " + cents + "/100";
		String amountInEnglish = dollarsInEnglish + centsString + " dollars";
		
		// finally, capitalize the first letter
		return amountInEnglish.substring(0,1).toUpperCase() + amountInEnglish.substring(1);
	}
	
	private static void validateAmountString(String amount)
	{
		if (amount.substring(0,1).equals("-"))
		{
			System.out.println("Only positive dollar values are allowed.");
			System.exit(1);
		}
		else if (!Pattern.matches("\\$?\\d{1,3}(,?\\d{3})*(\\.\\d{2})?", amount))
		{
			System.out.println(amount + " is not a supported dollar amount format.");
			System.exit(1);
		}
	}
}
