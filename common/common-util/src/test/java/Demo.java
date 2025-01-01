import java.util.HashMap;

/**
 * @author micro
 * @description
 * @date 2024/9/11 17:24
 * @github https://github.com/tokyokk
 */
public class Demo {

    // 冒泡排序
    public static void bubbleSort(final int[] arr) {
        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = 0; j < arr.length - i - 1; j++) {
                if (arr[j] > arr[j + 1]) {
                    final int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
    }

    // 模拟实现trim()方法，实现一
    public static void myTrim(final String str) {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) != ' ') {
                builder.append(str.charAt(i));
            } else {
                while (i < str.length() && str.charAt(i) == ' ') {
                    i++;
                }
                builder.append(' ');
            }
        }
    }

    // trim实现二
    public static String myTrim2(final String str) {
        int start = 0, end = str.length();
        while (start < end && str.charAt(start) == ' ') {
            start++;
        }
        while (start < end && str.charAt(end - 1) == ' ') {
            end--;
        }
        return str.substring(start, end);
    }

    // 将字符串反转
    public static String reverse(final String str) {
        final StringBuilder sb = new StringBuilder();
        for (int i = str.length() - 1; i >= 0; i--) {
            sb.append(str.charAt(i));
        }
        return sb.toString();
    }

    // 将字符串反转二
    public static String reverseStr(final String str) {
        final char[] chars = str.toCharArray();
        int start = 0, end = str.length() - 1;
        while (start < end) {
            final char temp = chars[start];
            chars[start] = chars[end];
            chars[end] = temp;
            start++;
            end--;
        }
        return new String(chars);
    }

    // 查找子串出现次数
    public static int findCount(String str, final String subStr) {
        int count = 0;
        int index = 0;
        while ((index = str.indexOf(subStr)) != -1) {
            count++;
            str = str.substring(index + subStr.length());
        }
        return count;
    }

    // 找出字符串中最长的公共前后缀
    public static String findCommonPrefix(final String str) {
        final StringBuilder sb = new StringBuilder();
        final char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == chars[chars.length - 1 - i]) {
                sb.append(chars[i]);
            } else {
                break;
            }
        }
        return sb.toString();
    }

    // 寻找两个字符串中的最长子串
    public static String findLongestSubStr(final String str1, final String str2) {
        final StringBuilder sb = new StringBuilder();
        final char[] chars1 = str1.toCharArray();
        final char[] chars2 = str2.toCharArray();
        for (final char value : chars1) {
            for (final char c : chars2) {
                if (value == c) {
                    sb.append(value);
                } else {
                    break;
                }
            }
        }
        return sb.toString();

    }


    // 两数之和
    public static int[] twoSum(final int[] nums, final int target) {
        final HashMap<Integer, Integer> map = new HashMap<>();// key：差值，value：索引
        for (int i = 0; i < nums.length; i++) {
            final int num = target - nums[i];
            if (map.containsKey(num)) {
                return new int[]{map.get(num), i};
            }
            map.put(nums[i], i);
        }
        return new int[]{-1, -1};
    }


}
