Rally
=====

-Exercise 1-

Write some code that will accept an amount and convert it to the appropriate string representation.
<br>Example: Convert 2523.04 to "Two thousand five hundred twenty-three and 04/100 dollars"


<h3>Notes</h3>
Expected usage:
  java -jar DollarsToWords.jar <amount> 

I tried to stick to native Java for ease of sharing, but ended up including Guava (bundled in the lib/ folder) because I wanted a Joiner for delimited string concatenation.
