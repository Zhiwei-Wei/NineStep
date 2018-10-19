package view.menu;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import solution.ClauseSolution;
import style.font.FontClass;

public class MenuFrame extends JFrame{
	/**
	 * 代码会因为编码为题出现大量错误，将java文件按照utf-8格式保存两次即可
	 */
	private static final long serialVersionUID = 1L;
	private static final int DEFAULT_WIDTH = 800;
	private static final int DEFAULT_HEIGHT = 600;
	private JPanel jp_input, jp_key, jp_output;
	private JTextField jtf_input;
	private JTextArea jta_left, jta_right;
	private JButton jb_submit,jb0,jb1,jb2,jb3,jb4,jb5,jb6,jb7,jb8,jb_next
	,jb_prev;
	private JLabel jlb_input;
	private Font[] fList;
	private String[] lstr;
	private ClauseSolution cs;
	private JScrollPane jsp_lft, jsp_rgt;
	private int cur_step = 0;
	private String[] button = {"∀","∃","(",")","→","┐","∧","∨","↔"};
	public MenuFrame() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException{
		UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		FontClass.loadIndyFont();
		jtf_input = new JTextField(20);
		jtf_input.setText(button[0]);
        //初始化Font列表
        lstr = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        fList = new Font[lstr.length];
        for(int i=0;i<lstr.length;i++)
        {
         fList[i]=new Font(lstr[i],jtf_input.getFont().getStyle(), jtf_input.getFont().getSize());
        }
        //监听输入的文本
        jtf_input.addCaretListener(new MyTextListener());
        jtf_input.setText("");
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);//长宽
		appearCenter();//居中显示
		setTitle("实验二：子句集9步法冲突消解-1522010231魏智伟");
		setResizable(false);
		setLayout(new BorderLayout());
		jp_input = new JPanel();
		jlb_input = new JLabel("请输入要转化的谓词公式");
		jp_output = new JPanel();
		jp_key = new JPanel();
		jb_submit = new JButton("提交");
		jp_key.setLayout(new GridLayout(3, 3));
		//输入栏以及提交按钮
		jp_input.add(jlb_input);
		jp_input.add(jtf_input);
		jp_input.add(jb_submit);
		//输入键盘
		jb0 = new JButton("任意");
		jb1 = new JButton("存在");
		jb2 = new JButton("(");
		jb3 = new JButton(")");
		jb4 = new JButton("单条件");
		jb5 = new JButton("非");
		jb6 = new JButton("合取");
		jb7 = new JButton("析取");
		jb8 = new JButton("双条件");
		JButton[] tmp = {jb0,jb1,jb2,jb3,jb4,jb5,jb6,jb7,jb8};
		for(int i = 0; i < 9; i++){
			String stmp = button[i];
			tmp[i].addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
	//				JOptionPane.showMessageDialog(null, jtf_input.getCaretPosition());
					int idx = jtf_input.getCaretPosition();
					String tmp = jtf_input.getText();
					jtf_input.setText(tmp.substring(0, idx)+stmp
							+tmp.substring(idx));
					jtf_input.requestFocus();
					jtf_input.setCaretPosition(idx+1);
				}
			});
			jp_key.add(tmp[i]);
		}
		jb_submit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				int t = JOptionPane.showConfirmDialog(null, "确认谓词公式准确无误吗？",
						"再检查一下吧",
						JOptionPane.YES_NO_OPTION);
				if(t==0){//确定了
					jtf_input.setEditable(false);
					cs = new ClauseSolution(jtf_input.getText().trim());
					jb_next.setEnabled(true);
					for(int i =0; i < tmp.length; i++){
						tmp[i].setEnabled(false);
					}
					((JButton)e.getSource()).setEnabled(false);
					if(!cs.run()){
						JOptionPane.showMessageDialog(null, "出现错误！推导无法进行！");
					}else{
						jta_left.setText(cs.clause);
					}
				}
			}
		});
		//输出框
		jta_left = new JTextArea(13,13);
		jsp_lft = new JScrollPane(jta_left);
		jta_right = new JTextArea(13,13);
		jsp_rgt = new JScrollPane(jta_right);
		jta_left.setLineWrap(true);
		jta_right.setLineWrap(true); 
		flushBorder();
		jb_prev = new JButton("<<<上一步");
		jb_next = new JButton("下一步>>>");
		jb_prev.setEnabled(false);
		jb_prev.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				cur_step--;
				flushBorder();
				if(!jb_next.isEnabled())
					jb_next.setEnabled(true);
				if(cur_step<=0){
					jb_prev.setEnabled(false);
				}
				jta_left.setText(cs.getResult(cur_step));
				jta_right.setText(cs.getResult(cur_step+1));
			}
		});
		jb_next.setEnabled(false);
		jb_next.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				cur_step++;
				flushBorder();
				if(cur_step>=9){//最大只有9步
					jb_next.setEnabled(false);
				}
				if(!jb_prev.isEnabled())
					jb_prev.setEnabled(true);
				jta_left.setText(cs.getResult(cur_step));
				jta_right.setText(cs.getResult(cur_step+1));
			}
		});
        jta_left.addCaretListener(new MyTextAreaListener());
        jta_right.addCaretListener(new MyTextAreaListener());
		jta_left.setEditable(false);
		jta_right.setEditable(false);
		jp_output.add(jsp_lft);
		jp_output.add(jb_prev);
		jp_output.add(jb_next);
		jp_output.add(jsp_rgt);
		jp_input.setPreferredSize(new Dimension(0, 40));
		jp_key.setPreferredSize(new Dimension(50, 150));
		jp_output.setPreferredSize(new Dimension(0, 380));
		this.add(jp_input, BorderLayout.NORTH);
		this.add(jp_key, BorderLayout.CENTER);
		this.add(jp_output, BorderLayout.SOUTH);
	}
	private void flushBorder() {
		// TODO Auto-generated method stub
		if(cur_step==0){
			jta_left.setBorder(BorderFactory.createTitledBorder("初始状态"));
		}else{
			jta_left.setBorder(BorderFactory.createTitledBorder("第"+(cur_step)+"步"));
		}
		if(cur_step==9){
			jta_right.setBorder(BorderFactory.createTitledBorder("子句集"));
		}else{
			jta_right.setBorder(BorderFactory.createTitledBorder("第"+(cur_step+1)+"步"));
		}
	}
	private class MyTextAreaListener implements CaretListener{
		@Override
		public void caretUpdate(CaretEvent e) {
			// TODO Auto-generated method stub
			JTextArea jtf = (JTextArea)e.getSource();
			jtf.setFont(fList[7]);
			
		}
	}
	private class MyTextListener implements CaretListener {    
		@Override
		public void caretUpdate(CaretEvent e) {
			// TODO Auto-generated method stub
			JTextField jtf = (JTextField)e.getSource();
			jtf.setFont(fList[7]);
			
		}    
    }
	private void appearCenter() {
		// TODO Auto-generated method stub
		int windowWidth = this.getWidth();                    //获得窗口宽
        int windowHeight = this.getHeight();                  //获得窗口高
        Toolkit kit = Toolkit.getDefaultToolkit();             //定义工具包
        Dimension screenSize = kit.getScreenSize();            //获取屏幕的尺寸
        int screenWidth = screenSize.width;                    //获取屏幕的宽
        int screenHeight = screenSize.height;                  //获取屏幕的高
        this.setLocation(screenWidth/2-windowWidth/2, 
        		screenHeight/2-windowHeight/2);//设置窗口居中显示
	}

}
