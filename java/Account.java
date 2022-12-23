package java期末;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

/**
 * 账号类
 * 用于管理账号
**/
public  class Account implements Comparable<Account>{
    private String account;//账号 主键
    private String password;//账号密码 主键
    static int afrom =1010000;//账号从1010000开始
    static int Safrom =20480000;//学生学号从20480000开始
    static int Tafrom =10240000;//教师工号从10240000开始

    //得到账号list的索引
    static int get_from(String numb,Account a){
        int x=-1;//索引位置
        int getnumb=Integer.parseInt(numb);
        //学生获取自身索引
        if(a instanceof Student){
            //学生账号数据库
            if(numb.equals(((Student) a).getNumb())){
                x= getnumb-Safrom;
            }
        }
        //教师获取学生索引或自身索引
        else if(a instanceof Teacher){
            //学生账号数据库
            if(getnumb>=Safrom){
                x= getnumb-Safrom;
            }
            //教师账号数据库
            else if(numb.equals(((Teacher) a).getNumb())){
                x= getnumb-Tafrom;
            }
        }
        //管理员获取账号索引
        else if(a instanceof Administrator){
            //学生账号数据库
            if(getnumb>=Safrom){
                x= getnumb-Safrom;
            }
            //教师账号数据库
            else if(getnumb>=Tafrom){
                x= getnumb-Tafrom;
            }
            //账号数据库
            else if(getnumb>=afrom){
                x= getnumb-afrom;
            }
        }
        return x;
    }

    //录入账号数据
    static void readaccount(List<Account> account, HashSet<String> dcdaccount, String get){
        String[] arr1=get.split("\t");//分割字符
        Account a=null;
        //账号格式：类名 账号 密码 号码 性别 姓名
        if(dcdaccount.add(arr1[1])){
            if(arr1[0].equals("Student")){
                a=new Student(arr1[1],arr1[2],arr1[3],arr1[4],arr1[5]);
            }
            else if(arr1[0].equals("Teacher")){
                a=new Teacher(arr1[1],arr1[2],arr1[3],arr1[4],arr1[5]);
            }
            if(a!=null){
                account.add(a);
            }
        }else{
            System.out.print("|账号重复："+arr1[1]);
        }
    }

    //账号表加载
    static void accountfileread(List<Account> account, HashSet<String> dcdaccount, String fileName){
        File f1=new File(fileName);
        if(f1.exists()) {
            try {
                //录入文件数据
                Scanner sc=new Scanner(new FileReader(fileName));
                //按行读取字符串
                System.out.print("账号表");
                //读取文件行数
                int lines=Main.getline(fileName);
                for (int i=0;i<lines;i++) {
                    String get = sc.nextLine();
                    readaccount(account,dcdaccount,get);
                }
                System.out.println("|加载完成");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        else{
            try {
                f1.createNewFile();
                System.out.println("账号文件创建成功");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //账号登录
    static Account login(List<Account> account,String act,String pass,Account user){

        if(Integer.parseInt(act)>=Tafrom||Integer.parseInt(act)<afrom) {
            System.out.println("账号出错");
            return null;
        }//判断act是否是账号
        int get=get_from(act,user);//获取账号索引
        if(get>=0){//判断密码是否正确
            Account a=account.get(get);
            if(a.getPassword().equals(pass)){
                System.out.println("登录成功|"+a.toString());
                return a;
            }
        }
        System.out.println("登录失败|密码出错");
        return null;
    }

    public Account() {
        }
    public Account(String account, String password) {
        this.account = account;
        this.password = password;
    }
    //该方法判断账号和账号密码是否正确
    @Override
    public boolean equals(Object o) {
            if(o==null)return false;
            if(o instanceof Account){
                Account a=(Account)o;
                return account.equals(a.account)&&password.equals(a.password);
            }
            return false;
    }
    //该方法用于账号排序比较
    @Override
    public int compareTo(Account a) {
        return account.compareTo(a.account);
    }

    @Override
    public String toString() {
        return "账号信息 [ 账号："+account+"\t密码："+password+" ]";
    }

    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }


}
class Student extends Account {
    private String numb;//学号 主键
    private String sex;//性别
    private String name;//姓名
    public Student() {
    }

    public Student(String account, String password, String numb, String sex, String name) {
        super(account, password);
        this.numb = numb;
        this.sex = sex;
        this.name = name;
    }
    //该方法用于学生账号查询成绩时分组该学生的成绩
    @Override
    public boolean equals(Object o) {
        if(o==null)return false;
        if(o instanceof Student){
            Student s=(Student) o;
            return numb.equals(s.numb);
        }
        return false;
    }
    //该方法用于教师或管理员查询学生成绩时的排序比较
    @Override
    public int compareTo(Account a) {
        if(a instanceof Student){
            Student s=(Student) a;
            return numb.compareTo(s.getNumb());
        }
        else if(a instanceof Teacher||a instanceof Administrator){
            return 1;
        }
       return 0;
    }
    //返回学生信息
    @Override
    public String toString() {
        return "学生信息 [ 学号："+numb+"\t姓名："+name+"\t性别："+sex+" ]";
    }

    public String getNumb() {
        return numb;
    }

    public String getName() {
        return name;
    }

}
class Teacher extends Account{
    private String numb;//工号 主键
    private String sex;//性别
    private String name;//姓名
    public Teacher() {
        ;
    }

    public Teacher(String account, String password, String numb, String sex, String name) {
        super(account, password);
        this.numb = numb;
        this.sex = sex;
        this.name = name;
    }


    //该方法用于教师账号管理学生成绩或查询课程时分组
    @Override
    public boolean equals(Object o) {
        if(o==null)return false;
        if(o instanceof Teacher){
            Teacher t=(Teacher) o;
            return this.numb.equals(t.numb);
        }
        return false;
    }
    ////该方法用于管理员查询教师课程时的排序比较
    @Override
    public int compareTo(Account a) {
        if(a instanceof Student){
            return -1;
        }
        else if(a instanceof Teacher){
            Teacher s=(Teacher) a;
            return numb.compareTo(s.getNumb());
        }
        else if(a instanceof Administrator){
            return 1;
        }
        return 0;
    }
    //返回教师信息
    @Override
    public String toString() {
        return "教师信息 [ 工号："+numb+"\t姓名："+name+"\t性别："+sex+" ]";
    }

    public String getNumb() {
        return numb;
    }

    public String getName() {
        return name;
    }

}
class Administrator extends Account{
    private String name;//姓名
    public Administrator(String account, String password, String name) {
        super(account, password);
        this.name = name;
    }
    @Override
    public String toString() {
        return "管理员信息 [ 名字：" + name + " ]";
    }
    @Override
    public int compareTo(Account a) {
         return -1;
    }
}