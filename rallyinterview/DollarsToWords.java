import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
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
	private static final String[] largeNumberLabels = 
		{ "", "thousand", "million", "billion", "trillion", "quadrillion", "quintillion" };

	private static final ArrayList<String> englishAmountParts = Lists.newArrayList();
	
	
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
			
			if (placeValue % 3 == 2) // we're at a relative hundreds place
			{
				// triplets of zeroes get skipped over verbally
				if (dollars.substring(i,i+3).equals("000"))
				{
					i += 2;
					continue;
				}
				
				if (digit == 0)
					continue;
				
				// add the number's name
				englishAmountParts.add(zeroTo19[0][digit]);
				englishAmountParts.add("hundred");
				
				// label even hundreds now or the ones place needs to backtrack 
				// to distinguish them from true zeroes 
				if (placeValue > 2 && dollars.substring(i+1,i+3).equals("00"))
				{
					addLargeNumberLabel(placeValue-2);
					i += 2;
				}
			}
			else if (placeValue % 3 == 1 && digit != 0) // a relative tens place
			{
				// Tens and ones places have to be translated to words together,
				// so peek ahead to the next digit
				int onesValue = Integer.parseInt(dollars.substring(i+1,i+2));
				
				// add the number's name
				if (digit == 1)
					englishAmountParts.add(zeroTo19[digit][onesValue]); 
				else if (onesValue == 0)
					englishAmountParts.add(tens[digit]);
				else
					englishAmountParts.add(tens[digit] + "-" + zeroTo19[0][onesValue]);

				// add label appropriate for the adjacent ones
				addLargeNumberLabel(placeValue-1); 
			
				i++;  // we just dealt with the imminent ones place, so skip it
			}
			else if (placeValue % 3 == 0) // a relative ones place
			{
				// Note: encountering a zero here means it's in the final ones place 
				// and there have been no other positive digits so far
				englishAmountParts.add(zeroTo19[0][digit]);
				addLargeNumberLabel(placeValue);
			}
		}
		
		String dollarsInEnglish = Joiner.on(" ").join(englishAmountParts);
		String amountInEnglish = dollarsInEnglish + " and " + cents + "/100 dollars";
		
		// finally, capitalize the first letter
		return amountInEnglish.substring(0,1).toUpperCase() + amountInEnglish.substring(1);
	}
	
	private static void addLargeNumberLabel(int placeValue) {
		
		// no labels below 10^3)
		if (placeValue < 3)
			return;
		
		// Do we know the name of this large amount?
		if ( placeValue/3 < largeNumberLabels.length-1)
		{
			englishAmountParts.add(largeNumberLabels[placeValue/3]);
		}
		else
		{
			System.err.println("The amount entered exceeds the supported maximum.");
			System.exit(1);
		}
	}
	
	private static void validateAmountString(String amount)
	{
		if (amount.substring(0,1).equals("-"))
		{
			System.out.println("Only positive dollar values are supported.");
			System.exit(1);
		}
		else if (  !Pattern.matches("\\$?[1-9]\\d{0,2}(,?\\d{3})*(\\.\\d{2})?", amount)
				&& !Pattern.matches("\\$?0(\\.\\d{2})?", amount) )
		{	// Intended pattern is: 
			// optional dollar sign then digits followed by (optionally comma-separated) 
			// triplets of digits until an optional decimal point with two cents digits
			System.out.println(amount + " is not a supported dollar amount format.");
			System.exit(1);
		}
	}
}
