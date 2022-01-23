public class PairTest {
    public static void main(String[] args) {
        String a = "2";
        String b = "6";
        System.out.println(stringAddition(a,b));

/*        Pair<String, String> pair1 = new Pair<>("if", "else");
        Pair<String, String> pair2 = new Pair<>(pair1);
        pair1.setFirst("elif");
        System.out.println(pair1);
        System.out.println(pair2);*/

    }

    public static String stringAddition(String a, String b){
        return Float.toString(Float.parseFloat(a) + Float.parseFloat(b));
    }
}
