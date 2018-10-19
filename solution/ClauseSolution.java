package solution;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//(∀x)((∀y)P(x,y)→┐(∀y)(Q(x,y)→R(x,y)))
//┐Q(x,y)→R(x,y)
public class ClauseSolution {
	public static String[] button = {"∀","∃","→","∧","∨","↔"};
	public static Pattern p1 = Pattern.compile("(∀\\w)"),//这里的正则表达式是错误的，括号应该要转义，结果歪打正着...
			p2 = Pattern.compile("(∃\\w)");//匹配约束辖域
	public String clause;
	private int cnt;
	private ArrayList<Character> variable, funcHead;
	private ArrayList<String> result;
	public ClauseSolution(String clause){
		result = new ArrayList<>();
		variable = new ArrayList<>();
		funcHead = new ArrayList<>();
		this.clause = clause;
		cnt = 0;
	}
	/**
	 * 如果在推导过程中出现错误，则返回false
	 * 将每一次推导的结果都存在result队列中，要包括换行符
	 * 最终的推导结果为一行行的子句集
	 * @return
	 */
	public boolean run(){
		//首先消去单条件和双条件
		//程序思路出错了，应该用迭代的思想，把一个句子中单条件之前
		//的部分和单条件之后的部分挑出来，然后对着两个部分再进行处理，
		//直到不含有单条件符号或者双条件符号为止
		//P，Q表达的思想应该是正确的，不过要考虑全程量词和存在量词，
		//最好能把句子一开始就分为几个部分，这样比较好处理
		//可以写一个init()预处理函数
		String step1 = new String(clause);
		step1 = addBrackets(step1);
		if(isStep1(step1)){
			step1 = step1(step1);
		}
		step1 = removeBrackets(step1);
		result.add(step1);
		//第二步
		//判断 非 之后是否直接跟着左括号，然后在判断跟的是辖域还是普通子句
		String step2 = new String(step1);
		if(isStep2(step2)){
			step2 = step2(step2);
		}
		result.add(step2);
		//第三步
		//要转换变元
		//可以考虑采用depth变量，记录括号数量
		//每用正则匹配一个 (Qx)( ，就代表深度+1
		//需要记录变元的个数，从而避免重复
		String step3 = new String(step2);
		step3 = step3(step3);
		result.add(step3);
		//第四步直接用正则表达式抓取并放到最前面即可
		String step4 = new String(step3);
		step4 = step4(step4);
		result.add(step4);
		//第五步消去存在量词，分两种情况套路
		//1.左边没有全称量词，那么直接去掉，然后把存在量词的变量都改为一个新的常量即可
		//2.左边有全称量词，用函数形式替换原有的存在量词即可，都可以用String.replace函数实现
		String step5 = new String(step4);
		step5 = step5(step5);
		result.add(step5);
		//第六步化为Skolem标准型
		String step6 = new String(step5);
		step6 = step6(step6);
		result.add(step6);
		//第七步消去全称量词，简单
		String step7 = new String(step6);
		step7 = step7(step7);
		result.add(step7);
		//第八步，消去合取
		String step8 = new String(step7);
		step8 = step8(step8);//用换行符顶替即可
		result.add(step8);
		String step9 = new String(step8);
		step9 = step9(step9);
		result.add(step9);
		result.add(step9);
		return true;
	}
	/**
	 * 对每个子句，先替换掉之前出现过的变量，然后再把替换之后的变量加入列表中
	 * 供下一句子句检查是否重复
	 * @param s
	 * @return
	 */
	private String step9(String s) {
		// TODO Auto-generated method stub
		String[] ss = s.split("\n");
		if(ss.length==1)
			return s;
		ArrayList<Character> var = new ArrayList<>();
		//先从第一句子句中获取其变量
		Pattern p = Pattern.compile("\\(\\w");
		Matcher m = p.matcher(ss[0]);//默认一个子句中只有一个变量吧……
		if(m.find()){
			var.add(m.group().charAt(1));
		}
		for(int i = 1; i < ss.length; i++){
			int tt = var.size();
			for(int j = 0; j < tt; j++){
				char c = var.get(j);
				if(ss[i].contains(c+"")){
					char tm = getVar(var);
					ss[i] = ss[i].replace(c, tm);
				}
			}
		}
		StringBuilder ans = new StringBuilder();
		ans.append(ss[0]);
		for(int i = 1; i < ss.length; i++){
			ans.append("\n"+ss[i]);
		}
		return ans.toString();
	}
	private String step8(String s) {
		// TODO Auto-generated method stub
		if(!s.contains("∧"))
			return s;
		String[] ss = s.split("∧");
		StringBuilder ans = new StringBuilder();
		for(int i = 0; i < ss.length; i++){
			if(ss[i].charAt(0)=='(')
				ans.append(ss[i].substring(1, ss[i].length()-1));
			else{
				ans.append(ss[i]);
			}
			ans.append("\n");
		}
		return ans.toString();
	}
	private String step7(String s) {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder(s);
		StringBuilder tmp = new StringBuilder();
		Pattern p = Pattern.compile("\\(∀\\w\\)");
		Matcher m = p.matcher(sb);
		if(m.find()){
			tmp.append(m.group());
			sb.delete(m.start(), m.end());
			m = p.matcher(sb);
		}else{
			return s;
		}
		sb.deleteCharAt(0);//消去全称量词之后别忘把最外层括号消除
		sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}
	/**
	 * 将谓词公式转换为子句合取的形式，要记得加括号
	 * @param s
	 * @return
	 */
	private String step6(String s) {
//		这一步出错了！
		if(!s.contains("∨(")){
			return s;
		}
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder(s);
		StringBuilder tmp = new StringBuilder();
		Pattern p = Pattern.compile("\\(∀\\w\\)");
		Matcher m = p.matcher(sb);
		if(m.find()){
			tmp.append(m.group());
			sb.delete(m.start(), m.end());
			m = p.matcher(sb);
		}//删除全称量词并保存在tmp中
		ArrayList<String> sto = new ArrayList<>();
		StringBuilder pre = new StringBuilder();
		for(int i = 0; i < sb.length(); i++){
			if(sb.charAt(i)=='('||sb.charAt(i)==')'){
				sto.add(sb.charAt(i)+"");
			}
			else if(isSpecial(""+sb.charAt(i))){
				sto.add(pre.toString());
				pre = new StringBuilder();
				if(sb.charAt(i)=='∨' && sb.charAt(i+1)=='('){//析取之后跟括号，代表里面一定有合取
					int end = nextPos(sb.toString(), i);
					String[] ss = sb.substring(i+2, end).split("∧");//马虎处理一下
					String P = sto.remove(sto.size()-1);//取得刚刚添加进去的一项
					sto.add("("+P+"∨"+ss[0]+")");
					for(int j = 1; j < ss.length; j++){
						sto.add("∧("+P+"∨"+ss[j]+")");
					}
					sto.add(")");
					i = end;
				}
			}else{
				int end = nextPos(sb.toString(), i-1);
				pre.append(sb.substring(i, end+1));
				i = end;
			}
		}
		StringBuilder ans = new StringBuilder();
		for(String st:sto)
			ans.append(st);
		ans.deleteCharAt(ans.length()-1);
		return tmp.toString()+ans.toString();
	}
	private String step5(String s) {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder(s);
		StringBuilder ss = new StringBuilder(s);
		ArrayList<Character> tmp = new ArrayList<>();
		Pattern p = Pattern.compile("\\(∃\\w\\)");//删除存在量词
		Matcher m = p.matcher(sb);
		if(m.find()){
			while(true){
				sb.delete(m.start(), m.end());
				m = p.matcher(sb);
				if(!m.find())
					break;
			}
		}else{
			return s;
		}
		p = Pattern.compile("\\((∀|∃)\\w\\)");
		m = p.matcher(ss);
		boolean f = true;
		if(m.find()){
			while(true){
				char c = ss.charAt(m.start()+1);
				char var = ss.charAt(m.start()+2);
				ss.delete(m.start(), m.end());
				if(c=='∀'){
					f = false;
					tmp.add(var);//添加之前出现过的全称变量
				}else{
					if(f){//第一种情况
						sb = replace(sb, var, getConst());//C表示常量
					}else{
						sb = replace(sb, var, getFunc(tmp));
					}
				}
				m = p.matcher(ss);
				if(!m.find())
					break;
			}
		}
		return sb.toString();
	}
	private StringBuilder replace(StringBuilder sb, char oldVar, String newVar) {
		// TODO Auto-generated method stub
		StringBuilder ss = new StringBuilder();
		for(int i = 0; i < sb.length(); i++){
			if(sb.charAt(i)==oldVar){
				ss.append(newVar);
			}else{
				ss.append(sb.charAt(i));
			}
		}
		return ss;
	}
	private String getFunc(ArrayList<Character> arr) {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder(getFuncHead()+"("+arr.get(0));
		for(int i = 1; i < arr.size(); i++){
			sb.append(","+arr.get(i));
		}
		sb.append(")");
		return sb.toString();
	}
	/**
	 * 获取函数的名称，要求不能和变量名重复
	 * @return
	 */
	private String getFuncHead() {
		// TODO Auto-generated method stub
		for(int i = 'f'; i <= 'z'; i++){
			if(!variable.contains((char)i)&&!funcHead.contains((char)i)){
				funcHead.add((char)i);
				return (char)i+"";
			}
		}
		return "f";
	}
	private String getConst() {
		// TODO Auto-generated method stub
		return "CONST"+cnt++;
	}
	private String step4(String s) {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder(s);
		StringBuilder ss = new StringBuilder();
		Pattern p = Pattern.compile("\\((∀|∃)\\w\\)");
		Matcher m = p.matcher(sb);
		if(m.find()){
			while(true){
				ss.append(m.group());//一边删除一边增加
				sb.delete(m.start(), m.end());
				m = p.matcher(sb);
				if(!m.find())
					break;
			}
		}
		ss.append(sb.toString());
		return ss.toString();
	}
	/**
	 * 变元标准化
	 * @param s
	 * @return
	 */
	private String step3(String s) {
		// TODO Auto-generated method stub
		for(int i = 0; i < s.length(); i++){
			Matcher m1 = p1.matcher(s.substring(i));
			if(m1.find()){
				if(!variable.contains(s.charAt(i+m1.start()+1)))
					variable.add(s.charAt(i+m1.start()+1));
			}
		}
		// TODO Auto-generated method stub
		for(int i = 0; i < s.length(); i++){
			Matcher m1 = p2.matcher(s.substring(i));
			if(m1.find()){
				if(!variable.contains(s.charAt(i+m1.start()+1)))
					variable.add(s.charAt(i+m1.start()+1));
			}
		}//添加variable中所有已有的约束变元
		s = solve3(s, 0);
		return s;
	}
	private String solve3(String s, int depth) {
		// TODO Auto-generated method stub
		Pattern p = Pattern.compile("\\((∀|∃)\\w\\)");
		Matcher m = p.matcher(s);
		ArrayList<Character> tmp = new ArrayList<>();
		if(!m.find())
			return s;
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < s.length(); i++){
			if(i+4<s.length()){
				m = p.matcher(s.substring(i,i+4));
				if(m.find()){
					sb.append(s.substring(i,i+4));
					char var = s.charAt(m.start()+i+2);
					boolean f = false;
					char tv = 0;
					if(tmp.contains(var)){
						tv = getVar(variable);
						sb.setCharAt(sb.length()-2, tv);
						f = true;
					}else{
						tmp.add(var);
					}
					if(s.charAt(i+m.end())=='('){
						int end = nextPos(s, i+m.end()-1);
						String sss = solve3(s.substring(i+m.end(),end+1), depth+1);
						if(f){
							sss = sss.replace(var, tv);
						}
						sb.append(sss);
						i = end;
					}else{//后面直接就跟了一个对象
						int end = nextPos(s, i+m.end()-1);
						String sss = s.substring(i+m.end(),end+1);
						if(f){
							sss = sss.replace(var, tv);
						}
						sb.append(sss);
						i = end;
					}
				}else{
					sb.append(s.charAt(i));
				}
			}else{
				sb.append(s.charAt(i));
			}
		}
		return sb.toString();
	}
	/**
	 * 根据列表中已有的变量获取新的变量，获取之后自动向列表中添加
	 * @param var
	 * @return
	 */
	private char getVar(ArrayList<Character> var) {
		// TODO Auto-generated method stub
		for(int i = 'a'; i <= 'z'; i++){
			if(!var.contains((char)i)){
				var.add((char)i);
				return (char)i;
			}
		}
		return 0;
	}
	private String step2(String s) {
		// TODO Auto-generated method stub
		Pattern tp1 = Pattern.compile("┐\\(∀\\w\\)");
		Pattern tp2 = Pattern.compile("┐\\(∃\\w\\)");
		Pattern tp3 = Pattern.compile("┐\\(");
		StringBuilder sb = new StringBuilder(s);
		for(int i = 0; i < sb.length(); i++){
			Matcher tm = tp1.matcher(sb.toString().substring(i));
			if(tm.find()){
				sb.deleteCharAt(tm.start()+i);
				sb.insert(tm.end()+i-1, "┐");
				sb.setCharAt(tm.start()+i+1, '∃');
			}else{
				break;
			}
		}
		for(int i = 0; i < sb.length(); i++){
			Matcher tm = tp2.matcher(sb.toString().substring(i));
			if(tm.find()){
				sb.deleteCharAt(tm.start()+i);
				sb.insert(tm.end()+i-1, "┐");
				sb.setCharAt(tm.start()+i+1, '∀');
			}else{
				break;
			}
		}//已经去除辖域的问题了
		//下面对子句进行处理
		for(int i = 0; i < sb.length(); i++){
			Matcher tm = tp3.matcher(sb.toString().substring(i));
			if(tm.find()){
				int end = nextPos(sb.toString(), tm.start()+i);
				String tmp = sb.substring(tm.start()+1+i, end+1);
				sb.delete(tm.start()+i, end+1);//全删了
				tmp = "("+not_clause(tmp.substring(1,tmp.length()-1))+")";
				sb.insert(tm.start()+i, tmp);//加进去进行非操作的子句
				i += tm.start()+tmp.length()-1;//这里可能有问题
			}
		}
		
		return sb.toString();
	}
	/**
	 * 对子句进行非操作，用递归完成
	 * @param s
	 * @return
	 */
	private String not_clause(String s) {
		// TODO Auto-generated method stub
		if(!(s.contains("∧")||s.contains("∨"))){//出口
			return not(s);
		}
		StringBuilder sb = new StringBuilder();
		for(int i = -1; i < s.length(); i++){
			if(i!=-1){
				if(s.charAt(i)=='∧'){
					sb.append("∨");
				}else if(s.charAt(i)=='∨')
					sb.append("∧");
			}
			int nx = nextPos(s, i);
			sb.append(not_clause(s.substring(i+1,nx+1)));
			i = nx;
		}
		return sb.toString();
	}
	private boolean isStep2(String s) {
		// TODO Auto-generated method stub
		Pattern tp = Pattern.compile("┐\\(");
		return tp.matcher(s).find();
	}
	private String removeBrackets(String s) {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder(s);
		for(int i = 0; i < sb.length(); i++){
			Matcher m = p1.matcher(sb.toString().substring(i));
			if(m.find()){
				sb.deleteCharAt(m.start()+i-1);
				int rh = nextPos(sb.toString(), i+m.end()-1);
				sb.deleteCharAt(rh);
				i = i+m.end()-1;
			}
		}
		for(int i = 0; i < sb.length(); i++){
			Matcher m = p2.matcher(sb.toString().substring(i));
			if(m.find()){
				sb.deleteCharAt(m.start()+i-1);
				int rh = nextPos(sb.toString(), i+m.end()-1);
				sb.deleteCharAt(rh);
				i = rh;
			}
		}
		return sb.toString();
	}
	/**
	 * 把约束变量的辖域和其谓词括在一起
	 * @param step1
	 * @return
	 */
	private String addBrackets(String s) {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder(s);
		for(int i = 0; i < sb.length(); i++){
			Matcher m = p1.matcher(sb.toString().substring(i));
			if(m.find()){
				sb.insert(i+m.start(), "(");
				int rh = nextPos(sb.toString(), i+m.end()+1);
				sb.insert(rh, ")");
				i = i+m.end()+1;
			}
		}
		for(int i = 0; i < sb.length(); i++){
			Matcher m = p2.matcher(sb.toString().substring(i));
			if(m.find()){
				sb.insert(i+m.start(), "(");
				int rh = nextPos(sb.toString(), i+m.end()+1);
				sb.insert(rh, ")");
				i = i+m.end()+1;
			}
		}
		return sb.toString();
	}
	private boolean isStep1(String clause) {
		// TODO Auto-generated method stub
		return clause.contains("→")||clause.contains("↔");
	}
	private String step1(String cl) {
		// TODO Auto-generated method stub
		if(!isStep1(cl))//出口
			return cl;
		//用括号来对语句分层，逐层下降
		ArrayList<String> arr = new ArrayList<>();
		for(int i = -1; i < cl.length(); i++){
			if(i!=-1 && isSpecial(cl.charAt(i)+"")){//这里要确认是否是"("或者")"
				arr.add(cl.charAt(i)+"");//i指向的一定是一个符号
			}
			if(i+1<cl.length()){
				int tmp = nextPos(cl, i);
				if(cl.charAt(i+1)=='(')//区别在于整个子句是否是包括在一个最外层的大括号内的
					arr.add("("+step1(cl.substring(i+2, tmp))+")");//但是保存的时候要加上括号
				else if(cl.charAt(i+1)=='┐' && cl.charAt(i+2)=='(')
					arr.add("┐("+step1(cl.substring(i+3, tmp))+")");
				else
					arr.add(step1(cl.substring(i+1, tmp+1)));
				if(i==-1){
					Matcher m1 = p1.matcher(cl.substring(0,3));
					Matcher m2 = p2.matcher(cl.substring(0,3));
					if(m1.find()||m2.find()){
						i = tmp-1;
					}else{
						i = tmp;
					}
				}else
					i = tmp;
			}
		}
		//分割完毕
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < arr.size(); i++){
			if(isSpecial(arr.get(i))){//如果是指定的特殊符号，如析取合取单条件之类的
				if(arr.get(i).equals("→")){
					arr.set(i, "∨");
					arr.set(i-1, not(arr.get(i-1)));//非P
				}else if(arr.get(i).equals("↔")){
					String P = arr.get(i-1);
					String Q = arr.get(i+1);
					arr.set(i-1, "("+P+"∧"+Q+")");
					arr.set(i, "∨");
					arr.set(i+1, "("+not(P)+"∧"+not(Q)+")");
				}
			}//否则就是子句（已经处理完条件符号的）
		}
		for(String s:arr)
			sb.append(s);
		return sb.toString();
	}
	private String not(String s) {
		// TODO Auto-generated method stub
		if(s.charAt(0)=='┐')
			return s.substring(1);
		else
			return "┐"+s;
	}
	private boolean isSpecial(String s) {
		// TODO Auto-generated method stub
		for(String ss:button){
			if(s.equals(ss))
				return true;
		}
		return false;
	}
	/**
	 * 以符号的位置作为pos
	 * 基本和prevPos一致，返回的是下一个组件到达的位置（即右括号的位置）
	 * @param cl
	 * @param pos
	 * @return
	 */
	private int nextPos(String cl, int pos) {
		// TODO Auto-generated method stub
		int cnt = 0;
		int i = pos+1;
//		Matcher m = p1.matcher(cl.substring(i,i+4));
//		while(m.find()){
//			i += m.end();
//			m = p1.matcher(cl.substring(i,i+4));
//		}
//		m = p2.matcher(cl.substring(i,i+4));
//		while(m.find()){
//			i += m.end();
//			m = p1.matcher(cl.substring(i,i+4));
//		}
		boolean f = false;//控制保证至少碰到第一个括号才返回
		for(; i < cl.length(); i++){
			if(cl.charAt(i)=='('){
				cnt++;
				f = true;
			}
			if(cl.charAt(i)==')')
				cnt--;
			if(cnt==0 && f)
				break;
		}
		return i;
	}
	/**
	 * 返回pos前一个符号的位置，用来确定谓词中元素的位置
	 * 通过左右括号来确定，所以要求输入的一定是带括号变量的谓词逻辑
	 * @param cl
	 * @param pos
	 * @return -1表示没找到
	 */
//	private int prevPos(String cl, int pos){
//		int cnt = 0;
//		int i = pos-1;
//		for(; i >= 0; i--){
//			if(cl.charAt(i)==')')
//				cnt++;
//			if(cl.charAt(i)=='(')
//				cnt--;
//			if(cnt==0)
//				break;
//		}
//		return i-1;
//	}
	public String getResult(int step){
		if(step==0)
			return clause;
		try{
			if(step==9)
				return result.get(8);
			return result.get(step-1);
		}catch(IndexOutOfBoundsException e){
			return "无法推出下一步！";
		}
	}
}
