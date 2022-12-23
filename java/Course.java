package java期末;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

public class Course implements Comparable<Course>{
    private Teacher teacher;//授课老师 主键
    private String name;//课程名字 主键
    private String begintime;//开课时间 格式 年月日
    private String endtime;//结课时间 格式 年月日
    private String readtime;//学生查询成绩时间 格式 年月日
    List<Grades> grades=new ArrayList<>();//学生成绩表
    HashSet<String> dcdgrades=new HashSet<>();//学生名单

    public Course() {
    }

    public Course(Teacher teacher, String name, String begintime, String endtime, String readtime) {
        this.teacher = teacher;
        this.name = name;
        this.begintime = begintime;
        this.endtime = endtime;
        this.readtime = readtime;
    }

    //课程表加载
    static void coursefileread(List<Course>course,HashSet<String> dcdcourse,List<Account> Taccount,String fileName,Account user){
        File f1=new File(fileName);
        if(f1.exists()) {
            try {
                //录入文件数据
                Scanner sc=new Scanner(new FileReader(fileName));
                //按行读取字符串
                System.out.print("课程表");
                int lines=Main.getline(fileName);
                for (int i=0;i<lines;i++) {
                    String get = sc.nextLine();
                    readcourse(course,dcdcourse,Taccount,get,user);
                }
                System.out.println("|加载完成");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        else{
            try {
                f1.createNewFile();
                System.out.println("课程表文件创建成功");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //录入课程表数据
    static void readcourse(List<Course>course,HashSet<String> dcdcourse,List<Account> Taccount,String get,Account user){
        String[] arr1=get.split("\t");//分割字符
        Course a=null;
        //课程表格式：教师工号	课程名字	开课时间	结课时间	查询成绩时间
        if(dcdcourse.add(arr1[0]+arr1[1])){
            int dcd=Account.get_from(arr1[0],user);
            if(dcd>=0){
                Account t=Taccount.get(dcd);
                a=new Course((Teacher) t,arr1[1],arr1[2],arr1[3],arr1[4]);
                course.add(a);
            }
            else{
                System.out.print("|教师工号错误"+arr1[0]);
            }
        }
        else {
            System.out.print("|课程错误"+arr1[0]+arr1[1]);
        }
    }

    @Override
    public boolean equals(Object o) {
        if(o==null)return false;
        //判断该课程是否属于o教师
        if(o instanceof Teacher){
            return teacher.equals(o);
        }
        //判断是否为管理员
        else if(o instanceof Administrator){
            return true;
        }
        return false;
    }
    @Override
    public int compareTo(Course o) {
        //按授课老师排序
        int dcd=0;
        if(o!=null) return teacher.compareTo(o.teacher);
        return dcd;
    }

    @Override
    public String toString() {
        return "课程信息 [ 课程名："+name+"\t教师名："+teacher.getName()+"\t开课时间："+begintime+"\t结课时间："+endtime
                +"\t查询时间："+readtime+" ]";
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public String getName() {
        return name;
    }

    //判断当前时间时间是否比gettime大
    static boolean dcdtime(String gettime){
        int dcd=-1;
        //获取当前时间
        LocalDate date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String time=date.format(formatter);
        dcd=time.compareTo(gettime);
        if(dcd<0){
            return false;
        }
        return true;
    }

    public List<Grades> getGrades() {
        return grades;
    }

    public String getBegintime() {
        return begintime;
    }

    public String getEndtime() {
        return endtime;
    }

    public String getReadtime() {
        return readtime;
    }
}

class Grades implements Comparable<Grades>{
    private Student student;//学生
    private int grades=0;//课程成绩

    public Grades() {
    }

    public Grades(Student student, int grades) {
        this.student = student;
        this.grades = grades;
    }

    //学生表加载
    static void gradesfileread(List<Grades> grades,HashSet<String> dcdgrades,List<Account> Saccount,String Name,Account user){
        String fileName="src\\java期末\\students\\"+Name+".txt";
        File f1=new File(fileName);
        if(f1.exists()) {
            try {
                //录入文件数据
                Scanner sc=new Scanner(new FileReader(fileName));
                //按行读取字符串
                System.out.print(Name+"学生表");
                //读取文件行数
                int lines=Main.getline(fileName);
                for (int i=0;i<lines;i++) {
                    String get = sc.nextLine();
                    readgrades(grades,dcdgrades,Saccount,get,user);
                }
                System.out.println("|加载完成");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        else{
            try {
                f1.createNewFile();
                System.out.println(Name+"创建成功");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //录入学生表数据
    static void readgrades(List<Grades> grades,HashSet<String> dcdgrades,List<Account> Saccount,String get,Account user){
        String[] arr1=get.split("\t");//分割字符
        //学生表格式：学号 成绩
        if(dcdgrades.add(arr1[0])){
            int dcd=Account.get_from(arr1[0],user);
            if(dcd>=0){
                Account t=Saccount.get(dcd);
                Grades a=new Grades((Student) t,Integer.parseInt(arr1[1]));
                grades.add(a);
            }
            else {
                System.out.print("|学号错误："+get);
            }
        }
        else{
            dcdgrades.remove(arr1[0]);
            System.out.print("|学号错误："+get);
        }
    }

    //该方法用于判断该成绩单是否属于o学生
    @Override
    public boolean equals(Object o){
        if(o==null)return false;
        if(o instanceof Student){
            return student.equals(o);
        }
        return false;
    }
    //该方法用于学生成绩排序
    @Override
    public int compareTo(Grades o) {
        return this.grades-o.grades;
    }

    @Override
    public String toString() {
        return "成绩表 [ 学生："+student.getName()+"\t学生成绩："+grades+" ]";
    }

    public String getGrades() {
        return "学号："+student.getNumb()+"\t名字："+student.getName();
    }

    public int getgrades(){return grades;}

    public void setGrades(int grades) {
        this.grades = grades;
    }

    public Student getStudent() {
        return student;
    }
}
