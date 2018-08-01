//g1744069 Keisuke Ikeda
import java.io.*;
import java.util.HashMap;
public class GradeChecker6 {
    HashMap<Integer, Double> exammap = new HashMap<>();
    HashMap<Integer, Double> assigmap = new HashMap<>();
    HashMap<Integer, Double> miniexammap = new HashMap<>();
    Integer lastnum = 0, count = 0;
    Double  allput = 0.0, put = 0.0, allmax = 0.0, allmin =100.0, max = 0.0, min = 100.0;
    Double grade1 = 0.0, grade2 = 0.0, grade3 = 0.0, grade4 = 0.0, grade5 = 0.0, grade6 = 0.0, grade7 = 0.0;

    void run(String[] args) throws IOException { //コマンドライン引数の入力
        if(args.length < 2){
            System.out.println("ERROR: 試験の成績ファイルが指定されていません．");  //引数が足りない場合などは強制終了
            System.out.printf("java GradeChecker5  [OPTIONS]%nOPTIONS%n   -exam        <EXAM.CSV>%n   -assignments <ASSIGNMENTS.CSV>%n   -miniexam    <MINIEXAM.CSV>%n   -output     <RESULT_FILE>%n");
            System.exit(0);
        }
        parse(args);
        Arguments arguments = parse(args);
        if(arguments.output == null){
            printer1();
        }else {
            File outfile = new File(arguments.output);
            writer1(outfile);
        }
    }

    Arguments parse(String[] args) throws IOException { //オプション
        Arguments arguments = new Arguments();
        for(Integer i = 0; i < args.length; i++){
            if(args[i].equals("-exam")){
                i++;
                arguments.exam = args[i];
            }else if(args[i].equals("-assignments")){
                i++;
                arguments.assignments = args[i];
            }else if(args[i].equals("-miniexam")){
                i++;
                arguments.miniexam = args[i];
            }else if(args[i].equals("-output")){
                i++;
                arguments.output = args[i];
            }
        }
        if(arguments.exam != null) {
            inputexamfile(arguments);
        }
        if(arguments.assignments != null){
            inputassignmentsfile(arguments);
        }
        if(arguments.miniexam != null){
            inputminiexamfile(arguments);
        }
        return arguments;
    }

    void inputexamfile(Arguments arguments) throws IOException{ //入力したexamファイルの実体化
        File examfile = new File(arguments.exam);
        initialize(examfile, 0);
    }

    void inputassignmentsfile(Arguments arguments) throws IOException{ //入力したassignmentファイルの実体化
        File assigfile = new File(arguments.assignments);
        initialize(assigfile, 1);
    }

    void inputminiexamfile(Arguments arguments) throws IOException{ //入力したminiexamファイルの実体化
        File miniexamfile = new File(arguments.miniexam);
        initialize(miniexamfile, 2);

    }



    void initialize(File file, Integer flag) throws IOException { //各ファイルの読み込み
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while((line = br.readLine()) != null){ //nullになるまでファイル内の行を読み込む
            getdata(line, flag);
        }
        br.close();
    }

    void getdata(String line, Integer flag){ //データを区切り各ファイル毎に処理する
        String[] data = line.split(",", -1); //コンマの前後でデータを区切る
        if(flag == 0) {
            getexamScore(data);
        }else if(flag == 1){
            getassignments(data);
        }else{
            getminiexams(data);
        }
        lastnum = Integer.valueOf(data[0]);
    }

    void getexamScore(String[] data){ //examfileをexammapに格納する
        exammap.put(Integer.valueOf(data[0]), Double.valueOf(data[1]));
    }

    void getassignments(String[] data){ //assigfileをassigapmに格納する
        Double sum = 0.0;
        for(Integer i = 1; i < data.length; i++){
            if(data[i].isEmpty()){
                sum = sum + 0.00;
            }else{
                sum = sum + Double.valueOf(data[i]);
            }
        }
        assigmap.put(Integer.valueOf(data[0]), sum);
    }

    void getminiexams(String[] data){ //miniexamfileの要素数を数え、miniexamapに格納する
        Double count = 0.0;
        for(Integer i = 1; i < data.length; i++){
            if(!data[i].isEmpty()){
                count++;
            }
        }
        miniexammap.put(Integer.valueOf(data[0]), count/14);
    }

    void gettotalScore(Student score, Integer id){ //特別ルール
        score.totalScore = ((70 * exammap.get(id)) / 100) + ((25 * assigmap.get(id)) / 60) + 5 * miniexammap.get(id);
        if((Math.ceil(score.examScore) >= 80)&&(score.examScore >= score.totalScore)){
            score.totalScore = score.examScore;
        }
    }

    Student createscore(Integer id){  //データの引き渡し
        Student score = new Student();
        emptyscore(score, id);
        score.examScore = exammap.get(id);
        score.assignmentScore = assigmap.get(id);
        score.miniexamScore = miniexammap.get(id);
        score.id = id;
        gettotalScore(score, id);
        gradejudge(score);
        stats(score);
        return score;
    }

    void emptyscore(Student score, Integer id){ //未入力のデータの補完をする
        if(this.miniexammap.get(id) == null){
            miniexammap.put(id, 0.000);
        }
        if(this.exammap.get(id) == null){
            exammap.put(id, 0.000);
            score.grade = "K";
            grade6++;
        }
        if(this.assigmap.get(id) == null){
            assigmap.put(id, 0.000);
        }
    }

    void gradejudge(Student score){ //成績を判定する。
        if(Math.ceil(score.totalScore) >= 90){
            score.grade = "秀";
            grade1++;
        }else if(Math.ceil(score.totalScore) >= 80){
            score.grade = "優";
            grade2++;
        }else if(Math.ceil(score.totalScore) >= 70){
            score.grade = "良";
            grade3++;
        }else if(Math.ceil(score.totalScore) >= 60){
            score.grade = "可";
            grade4++;
        }else if((Math.ceil(score.totalScore) < 60)&&(score.miniexamScore <= 0.5)&&(score.grade != "K")){
            score.grade = "※";
            grade7++;
        }else if(score.grade == null){
            score.grade = "不可";
            grade5++;
        }
    }

    void stats(Student score){ //statsの計算
        if(allmax <= Math.ceil(score.totalScore)){
            allmax = Math.ceil(score.totalScore);
        }
        if((max <= Math.ceil(score.totalScore))&&(Math.ceil(score.totalScore) >= 60)){
            max = Math.ceil(score.totalScore);
        }
        if(allmin >= Math.ceil(score.totalScore)){
            allmin = Math.ceil(score.totalScore);
        }
        if((min >= Math.ceil(score.totalScore))&&(Math.ceil(score.totalScore) >= 60)){
            min = Math.ceil(score.totalScore);
        }
        allput += Math.ceil(score.totalScore);
        if(Math.ceil(score.totalScore) >= 60){
            put += Math.ceil(score.totalScore);
            count++;
        }
    }

    void printer1() { //結果を出力する　学生成績
        for (Integer i = 1; i <= lastnum; i++) {
            Student score = this.createscore(i);
            System.out.printf("%3d, %2.1f, %2.8f, %2.1f, %2.6f, %s%n", score.id, Math.ceil(score.totalScore), score.examScore, score.assignmentScore, score.miniexamScore, score.grade);
        }
        printer2();
    }

    void printer2(){ //結果を出力する　総合成績
        Double classgetsum = grade1 + grade2 + grade3 + grade4;
        System.out.printf(" Avg: %2.4f(%2.4f)%n Max: %2.4f(%2.4f)%n Min: %2.4f(%2.4f)%n", allput/lastnum, put/count, allmax, max ,allmin, min);
        System.out.printf("単位取得率: %3.4f％ (%3.0f/%d)%n",  (classgetsum/lastnum)*100, classgetsum, lastnum);
        System.out.printf("  秀:%1.0f ( %3.4f％ (%1.0f/%d),  %3.4f％ (%1.0f/%3.0f))%n", grade1, (grade1/lastnum)*100,  grade1, lastnum, (grade1/classgetsum)*100,  grade1, classgetsum);
        System.out.printf("  優:%1.0f ( %3.4f％ (%1.0f/%d),  %3.4f％ (%1.0f/%3.0f))%n", grade2, (grade2/lastnum)*100,  grade2, lastnum, (grade2/classgetsum)*100,  grade2, classgetsum);
        System.out.printf("  良:%1.0f ( %3.4f％ (%1.0f/%d),  %3.4f％ (%1.0f/%3.0f))%n", grade3, (grade3/lastnum)*100,  grade3, lastnum, (grade3/classgetsum)*100,  grade3, classgetsum);
        System.out.printf("  可:%1.0f ( %3.4f％ (%1.0f/%d),  %3.4f％ (%1.0f/%3.0f))%n", grade4, (grade4/lastnum)*100,  grade4, lastnum, (grade4/classgetsum)*100,  grade4, classgetsum);
        System.out.printf("不可:%1.0f ( %3.4f％ (%1.0f/%d))%n", grade5, (grade5/lastnum)*100,  grade5, lastnum);
        System.out.printf("  Ｋ:%1.0f ( %3.4f％ (%1.0f/%d))%n", grade6, (grade6/lastnum)*100,  grade6, lastnum);
        System.out.printf("  ※ :%1.0f ( %3.4f％ (%1.0f/%d))%n", grade7, (grade7/lastnum)*100,  grade7, lastnum);
    }

    void writer1(File file) throws IOException { //結果をファイルに出力する　学生成績
        FileWriter filewriter = new FileWriter(file, true);
        String src;
        for(Integer i = 1; i <= lastnum; i++) {
            Student score = this.createscore(i);
            src = String.format("%3d, %2.1f, %2.8f, %2.1f, %2.6f, %s%n", score.id, Math.ceil(score.totalScore), score.examScore, score.assignmentScore, score.miniexamScore, score.grade);
            filewriter.write(src);
        }
        writer2(filewriter);
        filewriter.close();
    }

    void writer2(FileWriter filewriter) throws IOException { //結果をファイルに出力する　総合成績
        Double classgetsum = grade1 + grade2 + grade3 + grade4;
        String src;
        src = String.format(" Avg: %2.4f(%2.4f)%n Max: %2.4f(%2.4f)%n Min: %2.4f(%2.4f)%n", allput/lastnum, put/count, allmax, max ,allmin, min);
        filewriter.write(src);
        src = String.format("単位取得率: %3.4f％ (%3.0f/%d)%n",  (classgetsum/lastnum)*100, classgetsum, lastnum);
        filewriter.write(src);
        src = String.format("  秀:%1.0f ( %3.4f％ (%1.0f/%d),  %3.4f％ (%1.0f/%3.0f))%n", grade1, (grade1/lastnum)*100,  grade1, lastnum, (grade1/classgetsum)*100,  grade1, classgetsum);
        filewriter.write(src);
        src = String.format("  優:%1.0f ( %3.4f％ (%1.0f/%d),  %3.4f％ (%1.0f/%3.0f))%n", grade2, (grade2/lastnum)*100,  grade2, lastnum, (grade2/classgetsum)*100,  grade2, classgetsum);
        filewriter.write(src);
        src = String.format("  良:%1.0f ( %3.4f％ (%1.0f/%d),  %3.4f％ (%1.0f/%3.0f))%n", grade3, (grade3/lastnum)*100,  grade3, lastnum, (grade3/classgetsum)*100,  grade3, classgetsum);
        filewriter.write(src);
        src = String.format("  可:%1.0f ( %3.4f％ (%1.0f/%d),  %3.4f％ (%1.0f/%3.0f))%n", grade4, (grade4/lastnum)*100,  grade4, lastnum, (grade4/classgetsum)*100,  grade4, classgetsum);
        filewriter.write(src);
        src = String.format("不可:%1.0f ( %3.4f％ (%1.0f/%d))%n", grade5, (grade5/lastnum)*100,  grade5, lastnum);
        filewriter.write(src);
        src = String.format("  Ｋ:%1.0f ( %3.4f％ (%1.0f/%d))%n", grade6, (grade6/lastnum)*100,  grade6, lastnum);
        filewriter.write(src);
        src = String.format("  ※ :%1.0f ( %3.4f％ (%1.0f/%d))%n", grade7, (grade7/lastnum)*100,  grade7, lastnum);
        filewriter.write(src);
    }

    public static void main(String[] args) throws IOException {
        GradeChecker6 gradeChecker6 = new GradeChecker6();
        gradeChecker6.run(args);
    }
}

