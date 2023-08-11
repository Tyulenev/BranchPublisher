package utils;

import lombok.extern.java.Log;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;

@Log
public class IntegerSetAndStringConverter {

    private final Pattern patternForCheckSetInString = Pattern.compile("(\\d{1,10},)*(\\d{1,10}$)");

    public Set<Integer> convertStringToIntegerSet(String setInString) {
        Set<Integer> setOfIntegers = new HashSet<>();
        try {
            for (String integerInString : setInString.split(",")) {
                setOfIntegers.add(Integer.parseInt(integerInString));
            }
        } catch (Exception ex1) {
            log.info("Exception in func convertStringToIntegerSet: " + ex1.getMessage());
        }
        return setOfIntegers;
    }

    public String convertFromIntegerSetToString(Set<Integer> integers) {
        StringBuilder stringBuilder = new StringBuilder();
        Iterator<Integer> integerIterator = integers.iterator();
        while(integerIterator.hasNext()) {
            Integer integer = integerIterator.next();
            if(!integerIterator.hasNext()) {
                stringBuilder.append(integer);
            } else {
                stringBuilder.append(integer).append(",");
            }
        }
        return stringBuilder.toString();
    }

    public boolean checkStringOnIntegerSet(String setInString) {
        return patternForCheckSetInString.matcher(setInString).matches();
    }

}
