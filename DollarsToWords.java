import com.google.common.base.Joiner;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Converts a positive, numeric dollar value to the corresponding English-language name.
 * Example: 2523.04 becomes "Two thousand five hundred twenty-three and 04/100 dollars"
 */
public class DollarsToWords {

	// All the English number words 
	private static final String[][] zeroTo19 = { 
		{"zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine"}, 
		{"ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eighteen", "nineteen"}
	};
	private static final String[] tens = 
		{ "", "ten", "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety" };
	private static final String[] largeOnes = 
		{ "", "thousand", "million", "billion", "trillion", "quadrillion", "quintillion" };
	
	
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
		ArrayList englishAmountParts = new ArrayList();
		String cents = "00";
		String dollars;
		
		// Tidy up amount string down to bare bones
		String tidyAmount = amount.replaceAll("[\\$,]", "");
		
		// if we have cents, save it for the end
		if (Pattern.matches("\\d+\\.\\d{2}", tidyAmount))
		{
			String[] dollarsAndCents = tidyAmount.split("\\.");
			dollars = dollarsAndCents[0];
			cents = dollarsAndCents[1];
		} 
		else 
		{
			dollars = tidyAmount;  // no cents, carry on
		}
		
		
		// Now traverse the number string, 
		// translating as we go based on the relative place value of each digit
		int length = dollars.length();
		for (int i=0; i < length; i++)
		{
			// placeValue is the power of 10 at a given position i
			int placeValue = length - i - 1; 
			int digit = Integer.parseInt(dollars.substring(i,i+1)); // digit at i
			
			if (placeValue % 3 == 0) // we're at a relative ones place
			{
				// fetch the number word
				englishAmountParts.add(zeroTo19[0][digit]);
				
				// Do we have a label for this large amount?
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
			else if (placeValue % 3 == 1) // a relative tens place
			{
				// Tens and ones places have to be translated to words together,
				// so peek ahead to the next digit
				int onesValue = Integer.parseInt(dollars.substring(i+1,i+2));
				
				// the number's name
				if (digit == 0 || digit == 1)
					englishAmountParts.add(zeroTo19[digit][onesValue]); 
				else
					englishAmountParts.add(tens[digit] + "-" + zeroTo19[0][onesValue]);
				
				// the number's label (no labels for actual ones place)
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
		String amountInEnglish = dollarsInEnglish + " and " + cents + "/100 dollars";
		
		// finally, capitalize the first letter
		return amountInEnglish.substring(0,1).toUpperCase() + amountInEnglish.substring(1);
	}
	
	private static void validateAmountString(String amount)
	{
		if (amount.substring(0,1).equals("-"))
		{
			System.out.println("Only positive dollar values are supported.");
			System.exit(1);
		}			 
		else if (!Pattern.matches("\\$?\\d{1,3}(,?\\d{3})*(\\.\\d{2})?", amount))
		{	// Intended pattern is: 
			// optional dollar sign then digits followed by (optionally comma-separated) 
			// triplets of digits until an optional decimal point with two cents digits
			System.out.println(amount + " is not a supported dollar amount format.");
			System.exit(1);
		}
	}
}
