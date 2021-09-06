import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.text.DateFormat; 
import java.time.LocalDateTime;
import java.util.Date;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import java.util.Properties;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.JFormattedTextField.AbstractFormatter;
import java.text.ParseException;

class CalenderCore
{
	static DateTimeFormatter dateFormatDayView, dateFormatMonthView;
	static LocalDateTime currentDate, eventDate;
	static JFrame parentFrame;
	static JPanel leftPanel, rightPanel, travelPanel, addAppointmentPanel, startTimePanel, endTimePanel;
	static JMenuBar menuBar;
	static JMenu fileMenu, viewMenu;
	static JMenuItem exit;
	static JRadioButtonMenuItem dayView, monthView;
	static JLabel statusLabel, contentAreaLabel;
	static JButton today, prev, next, addAppointment;
	static JTextField appointmentName;
	static JSpinner startTimeHH, startTimeMM, endTimeHH, endTimeMM;
	static boolean isDayView;
	
	public CalenderCore()
	{
		dateFormatDayView = DateTimeFormatter.ofPattern("MM/dd/yyyy"); 
		dateFormatMonthView = DateTimeFormatter.ofPattern("MM/yyyy"); 
		currentDate = LocalDateTime.now(); 
		parentFrame = new JFrame("Calender");
		menuBar = new JMenuBar();
		fileMenu = new JMenu("File"); viewMenu = new JMenu("View");
		exit = new JMenuItem("Exit");
		dayView = new JRadioButtonMenuItem("Day"); dayView.setSelected(true); // Set Day View selected by Default.
		isDayView = true;
		monthView = new JRadioButtonMenuItem("Month");
		statusLabel = new JLabel("Welcome"); contentAreaLabel = new JLabel(dateFormatDayView.format(currentDate));
		today = new JButton("Today"); prev = new JButton("<"); next = new JButton(">"); addAppointment = new JButton("Add Appointment");
		appointmentName = new JTextField(50); 
		leftPanel = new JPanel(); rightPanel = new JPanel(); travelPanel = new JPanel(); addAppointmentPanel = new JPanel();
		startTimePanel = new JPanel(); endTimePanel = new JPanel();
		startTimeHH = new JSpinner(new SpinnerNumberModel(00, 00, 23, 1));
		startTimeMM = new JSpinner(new SpinnerNumberModel(00, 00, 59, 1));
		endTimeHH = new JSpinner(new SpinnerNumberModel(00, 00, 23, 1));
		endTimeMM = new JSpinner(new SpinnerNumberModel(00, 00, 59, 1));
	}
	public static void createAndShowGUI()
	{
		parentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		/* 
			START of Menu Bar Configs.
		*/
		exit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				parentFrame.dispose();
			}
		});
		fileMenu.add(exit);
		viewMenu.add(dayView); viewMenu.add(monthView);
		monthView.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				statusLabel.setText("Clicked Month View");
				isDayView = false;
				contentAreaLabel.setText(dateFormatMonthView.format(currentDate));
			}
		});
		dayView.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				statusLabel.setText("Clicked Day View");
				isDayView = true;
				contentAreaLabel.setText(dateFormatDayView.format(currentDate));
			}
		});
		menuBar.add(fileMenu); menuBar.add(viewMenu);
		/* 
			END of Menu Bar Configs.
		*/

		

		/* 
			START of Left Panel Configs.
		*/
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.PAGE_AXIS));
		travelPanel.setLayout(new FlowLayout());
		travelPanel.add(prev); travelPanel.add(next);
		prev.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(isDayView)
				{
					statusLabel.setText("Clicked Previous Day");
					currentDate = currentDate.minusDays(1);
					contentAreaLabel.setText(dateFormatDayView.format(currentDate));
				}
				else
				{
					statusLabel.setText("Clicked Previous Month");
					currentDate = currentDate.minusMonths(1);
					contentAreaLabel.setText(dateFormatMonthView.format(currentDate));
				}
			}
		});
		next.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(isDayView)
				{
					statusLabel.setText("Clicked Next Day");
					currentDate = currentDate.plusDays(1);
					contentAreaLabel.setText(dateFormatDayView.format(currentDate));
				}
				else
				{
					statusLabel.setText("Clicked Next Month");
					currentDate = currentDate.plusMonths(1);
					contentAreaLabel.setText(dateFormatMonthView.format(currentDate));
				}
			}
		});
		today.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				statusLabel.setText("Clicked Today");
				currentDate = LocalDateTime.now();
				if(isDayView)
					contentAreaLabel.setText(dateFormatDayView.format(currentDate));
				else
					contentAreaLabel.setText(dateFormatMonthView.format(currentDate));
			}
		});
		leftPanel.add(today); leftPanel.add(travelPanel); leftPanel.add(addAppointment);
		// Implementing a "sophisticated date and time picker" using JDatePicker (https://jdatepicker.org/about/) and the JSpinner Class.
		UtilDateModel model = new UtilDateModel();
		Properties p = new Properties();
		p.put("text.today", "Today");
		p.put("text.month", "Month");
		p.put("text.year", "Year");
		JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
	    JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
	    addAppointmentPanel.setLayout(new BoxLayout(addAppointmentPanel, BoxLayout.PAGE_AXIS));
		startTimePanel.setLayout(new FlowLayout()); endTimePanel.setLayout(new FlowLayout());
		startTimePanel.add(startTimeHH); startTimePanel.add(startTimeMM); 
		endTimePanel.add(endTimeHH); endTimePanel.add(endTimeMM); 
		addAppointmentPanel.add(appointmentName); addAppointmentPanel.add(datePicker); 
		addAppointmentPanel.add(startTimePanel); addAppointmentPanel.add(endTimePanel);
		addAppointment.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				statusLabel.setText("Add Appointment Clicked");
				appointmentName.setText("New Event"); // Set Default Event Name
				int result = JOptionPane.showConfirmDialog(null, addAppointmentPanel, "Add Appointment", JOptionPane.OK_CANCEL_OPTION);
				if (result == JOptionPane.OK_OPTION) {
					Date selectedDate = (Date) datePicker.getModel().getValue();
				    DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
				    String startTime = startTimeHH.getValue() + ":" + startTimeMM.getValue();
				    String endTime = endTimeHH.getValue() + ":" + endTimeMM.getValue();
				    String appointmentReport = "Scheduled " + appointmentName.getText() + " on " + df.format(selectedDate) + " from " + startTime + " to "+ endTime+".";
				    statusLabel.setText(appointmentReport);
				}
				else
					statusLabel.setText("Cancelled creation of new appointment!");
			}
		});
		/* 
			END of Left Panel Configs.
		*/



		/* 
			Right Panel Configs.
		*/
		rightPanel.add(contentAreaLabel);

		

		/* 
			START of Final placement of components onto out frame.
		*/
		parentFrame.setJMenuBar(menuBar);
		parentFrame.setLayout(new BorderLayout());
		parentFrame.add(leftPanel, BorderLayout.WEST);
		parentFrame.add(statusLabel, BorderLayout.SOUTH);
		parentFrame.add(rightPanel, BorderLayout.EAST);
		/* 
			END of Final placement of components onto out frame.
		*/



		// Telling our calender app to assume required shape for accomodating all components, and then instructing it to display.
		parentFrame.pack();
		parentFrame.setVisible(true);
	}
	public static void main(String[] args)
	{  
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				CalenderCore calender = new CalenderCore();
				createAndShowGUI();
			}
		});
	}
}



/*
	Added a custom date formatter to pass into the JDatePicker functionality.
*/
class DateLabelFormatter extends AbstractFormatter {

    private String datePattern = "MM/dd/yyyy";
    private SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);

    @Override
    public Object stringToValue(String text) throws ParseException {
        return dateFormatter.parseObject(text);
    }

    @Override
    public String valueToString(Object value) throws ParseException {
        if (value != null) {
            Calendar cal = (Calendar) value;
            return dateFormatter.format(cal.getTime());
        }

        return "";
    }

}