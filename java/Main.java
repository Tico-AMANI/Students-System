package java期末;

import java.io.*;
import java.util.*;

public class Main {
    static HashSet<String> dcdaccount=new HashSet<>();//账号号码哈希表
    static List<Account> account=new ArrayList<>();//账号表
    static List<Account> Saccount=new ArrayList<>();//学生账号表
    static List<Account> Taccount=new ArrayList<>();//教师账号表
    static HashSet<String> dcdcourse=new HashSet<>();//课程表哈希表
    static List<Course>course=new ArrayList<>();//课程表
    static Administrator user;

    //初始化
    static {
        System.out.println("---------------------------");
        System.out.println("学生管理系统|作者：Tico");
        //录入管理员账号
        user=new Administrator("1010000","1024","Tico");
        account.add(user);
        //加载账号表
        String fileName="src\\java期末\\account\\account.txt";
        Account.accountfileread(account,dcdaccount,fileName);
        //分组账号
        for (Account account1 : account) {
            if(account1 instanceof Student){
                Saccount.add(account1);
            }
            else if(account1 instanceof Teacher){
                Taccount.add(account1);
            }
        }
        Collections.sort(Saccount);
        Collections.sort(Taccount);
        //加载课程表
        fileName="src\\java期末\\course\\course.txt";
        Course.coursefileread(course,dcdcourse,Taccount,fileName,user);
        //加载学生表
        for (Course course1 : course) {
            Grades.gradesfileread(course1.grades,course1.dcdgrades,Saccount,
                    course1.getTeacher().getNumb()+course1.getName(),
                    course1.getTeacher());
        }
    }

    public static void main(String[] args) {
        //登录账号
        Account a=loginwin();
        //账号操作
        while (a!=null){
            //管理员|添加课程
            if(a instanceof Administrator){
                a=awin(a);
            }
            //学生|查成绩单
            else if(a instanceof Student){
                a=swin(a);
            }
            //老师|查改成绩单|查授课
            else if(a instanceof Teacher){
                a=twin(a);
            }
        }
        //保存课程
        String fname="src\\java期末\\course\\course.txt";//课程的文件路径
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(fname));//打开文件
            //覆盖文件
            for (Course course1 : course) {
                out.write(course1.getTeacher().getNumb()+"\t"+
                        course1.getName()+"\t"+course1.getBegintime()+
                        "\t"+course1.getEndtime()+"\t"+course1.getReadtime()+"\n");
            }
            out.close();//关闭文件
        } catch (IOException e) {
        }
        //保存课程学生
        fname="src\\java期末\\students\\";//学生路径
        try {
            for (Course course1 : course) {
                //获取学生表名字
                String f=fname+course1.getTeacher().getNumb()+course1.getName()+".txt";
                BufferedWriter out = new BufferedWriter(new FileWriter(f));
                //打印学生成绩
                for (Grades grade : course1.getGrades()) {
                    out.write(grade.getStudent().getNumb()+"\t"+grade.getgrades()+"\n");
                }
                out.close();//关闭文件
            }

        } catch (IOException e) {
        }
    }

    //读取文件行数
    static int getline(String filename){
        int lines=0;
        try {
            File file5=new File(filename);
            if(file5.exists()) {
                long fileLength = file5.length();
                LineNumberReader lineNumberReader =new LineNumberReader(new FileReader(file5));
                lineNumberReader.skip(fileLength);
                lines=lineNumberReader.getLineNumber();
                lineNumberReader.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lines;
    }
    //登录界面 登录 退出
    static Account loginwin(){
        System.out.println("---------------------------");
        System.out.print("登录界面|1 登录|2 退出|输入：");
        Scanner sc=new Scanner(System.in);
        int get=sc.nextInt();
        if(get==1){
            System.out.print("输入账号|");
            String act=sc.next();
            System.out.print("输入密码|");
            String password=sc.next();
            Account a=Account.login(account,act,password,user);
            if(a==null) return loginwin();
            return a;
        }
        System.out.println("退出成功");
        return null;

    }
    //管理员界面
    static Account awin(Account a){
        System.out.println("---------------------------");
        System.out.print("|1 录入课程|2 录入学生|3 退出|输入：");
        Scanner sc=new Scanner(System.in);
        int get=sc.nextInt();
        //1 录入课程
        if(get==1){
            //录入课程表
            System.out.print("输入课程文件路径|");
            String fileName=sc.next();
            Course.coursefileread(course,dcdcourse,Taccount,fileName,a);
            //返回界面
            return awin(a);
        }
        //2 录入学生
        else if(get==2){
            //查询课程
            System.out.print("输入教师工号|");
            String tnumb=sc.next();
            List<Course>newcourse=new ArrayList<>();//教师课程分组
            boolean dcd=true;
            for (Course course1 : course) {
                if(course1.getTeacher().getNumb().equals(tnumb)){
                    dcd=false;
                    newcourse.add(course1);//添加课程
                    System.out.println(course1);//打印教师课程
                }
            }
            //判断教师是否授课
            if(dcd){
                System.out.println("教师"+tnumb+"没有授课");
            }
            else{
                System.out.println("---------------------------");
                //录入学生表
                System.out.print("输入课程名|");
                String Name=sc.next();
                for (Course course1 : newcourse) {
                    if(course1.getName().equals(Name)){
                        Grades.gradesfileread(course1.grades,course1.dcdgrades,Saccount,
                                course1.getTeacher().getNumb()+course1.getName(),
                                course1.getTeacher());//添加学生表
                    }
                }
            }
            //返回界面
            return awin(a);
        }
        //退出界面
        System.out.println("退出成功");
        return loginwin();
    }
    //教师|成绩单|查授课
    static Account twin(Account a) {
        System.out.println("---------------------------");
        System.out.print("|1 登记成绩|2 查询授课|3 退出|输入：");
        Scanner sc=new Scanner(System.in);
        int get=sc.nextInt();
        //登记成绩单
        if(get==1){
            List<Course>newcourse=new ArrayList<>();//课程表
            HashSet<String> newdcdcourse=new HashSet<>();//课程表哈希表
            //查询授课课程
            for (Course course1 : course) {
                if(course1.equals(a)){
                    newdcdcourse.add(course1.getName());//添加课程识别
                    newcourse.add(course1);//添加课程
                    System.out.println(course1.toString());
                }
            }
            //选择修改课程
            System.out.println("---------------------------");
            System.out.print("输入修改课程名：");
            String name=sc.next();
            if(!newdcdcourse.add(name)){
                //查找对应课程
                Course dcdcourse=null;
                for (Course course1 : newcourse) {
                    if (course1.getName().equals(name)) {
                        dcdcourse = course1;
                    }
                }
                //登记成绩
                if(Course.dcdtime(dcdcourse.getEndtime())&&!Course.dcdtime(dcdcourse.getReadtime())){
                    //批量赋分
                    List<Grades> grades=dcdcourse.getGrades();
                    for (Grades grade : grades) {
                        System.out.print(grade.getGrades()+"\t分数：");
                        grade.setGrades(sc.nextInt());
                    }
                }
                else{
                    System.out.println("课程"+name+"不在登记时间内");
                }
            }
            else{
                System.out.println("课程"+name+"不存在");
            }
            //返回界面
            return twin(a);
        }
        //查询授课
        else if(get==2){
            //查询授课课程
            for (Course course1 : course) {
                if(course1.equals(a)){
                    System.out.println(course1.toString());
                }
            }
            //返回界面
            return twin(a);
        }
        //退出界面
        System.out.println("退出成功");
        return loginwin();
    }
    //学生|查成绩单
    static Account swin(Account a){
        System.out.println("---------------------------");
        System.out.print("|1 查询成绩|2 退出|输入：");
        Scanner sc=new Scanner(System.in);
        int get=sc.nextInt();
        if(get==1){
            //显示成绩
            Student s=(Student)a;
            for (Course course1 : course) {
                //判断该课程是否有该学生
                if(!course1.dcdgrades.add(s.getNumb())){
                    //该课程包括该学生,且时间到达查询成绩,打印成绩
                    for (Grades grade : course1.getGrades()) {
                        if(grade.equals(s)){
                            if(Course.dcdtime(course1.getReadtime())){
                                //打印成绩
                                System.out.println("[ 课程名："+course1.getName()+"\t教师："
                                        +course1.getTeacher().getName()+"\t成绩："+grade.getgrades()+" ]");
                            }
                            else{
                                //打印查询时间
                                System.out.println("[ 课程名："+course1.getName()+"\t教师："
                                        +course1.getTeacher().getName()+"\t查询时间："+course1.getReadtime()+" ]");
                            }
                        }
                    }
                }
                else{
                    //该课程不包括该学生，然后删除添加的学号
                    course1.dcdgrades.remove(s.getNumb());
                }
            }
            //返回界面
            return swin(a);
        }
        //退出界面
        return loginwin();
    }

}
