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
	static JPanel leftPanel, rightPanel, travelPanel, addAppointmentPanel, startTimePanel, endTimePanel, appointmentTypePanel, todayPanel, addAppointmentButtonPanel, statusPanel;
	static JMenuBar menuBar;
	static JMenu fileMenu, viewMenu;
	static JMenuItem exit;
	static JRadioButtonMenuItem dayView, monthView;
	static JLabel statusLabel, contentAreaLabel, dialogBoxNameLabel, dialogBoxDateLabel, dialogBoxStartTimeLabel, dialogBoxEndTimeLabel;
	static JButton today, prev, next, addAppointment;
	static JTextField appointmentName;
	static JSpinner startTimeHH, startTimeMM, endTimeHH, endTimeMM;
	static JCheckBox vacationEvent, workEvent, meetingEvent, familyEvent, schoolEvent; 
	static ButtonGroup viewGroup;
	static boolean isDayView;
	static String dayViewTimeDisplay, monthViewTimeDisplay;
	
	public CalenderCore()
	{
		// Label-related variables
		statusLabel = new JLabel("Welcome");
		statusLabel.setOpaque(true);
		statusLabel.setBackground(Color.WHITE);
		dialogBoxNameLabel = new JLabel("Appointment Name");
		dialogBoxDateLabel = new JLabel("Date");
		dialogBoxStartTimeLabel = new JLabel("Start Time (in HH:MM)");
		dialogBoxEndTimeLabel = new JLabel("End Time (in HH:MM)");

		// Date-related variables
		dateFormatDayView = DateTimeFormatter.ofPattern("MM/dd/yyyy"); 
		dateFormatMonthView = DateTimeFormatter.ofPattern("MM/yyyy");
		dayViewTimeDisplay = "Day View: "; monthViewTimeDisplay = "Month Vew: "; 
		currentDate = LocalDateTime.now(); 

		// Frames and panels variables (Necessary for maintaining hierarchical control)
		parentFrame = new JFrame("GT Calender");
		leftPanel = new JPanel(); rightPanel = new JPanel(); travelPanel = new JPanel(); addAppointmentPanel = new JPanel();
		travelPanel.setOpaque(true);
		travelPanel.setBackground(Color.DARK_GRAY);
		startTimePanel = new JPanel(); endTimePanel = new JPanel(); appointmentTypePanel = new JPanel();
		todayPanel = new JPanel(); addAppointmentButtonPanel = new JPanel(); statusPanel = new JPanel();
		todayPanel.setOpaque(true);
		todayPanel.setBackground(Color.DARK_GRAY);
		addAppointmentButtonPanel.setOpaque(true);
		addAppointmentButtonPanel.setBackground(Color.DARK_GRAY);

		// Menu and menu items variables
		menuBar = new JMenuBar();
		fileMenu = new JMenu("File"); viewMenu = new JMenu("View");
		exit = new JMenuItem("Exit");
		dayView = new JRadioButtonMenuItem("Day"); dayView.setSelected(true); // Set Day View selected by Default.
		isDayView = true;
		monthView = new JRadioButtonMenuItem("Month");
		viewGroup = new ButtonGroup();

		// Content area variable
		contentAreaLabel = new JLabel(dayViewTimeDisplay + dateFormatDayView.format(currentDate));

		// Control area (left panel) variables
		today = new JButton("Today"); prev = new JButton("< Prev"); next = new JButton("Next >"); 
		addAppointment = new JButton("Add Appointment");
		
		// Add appointment dialog box variables
		appointmentName = new JTextField(50); appointmentName.setText("New Appointment"); 
		startTimeHH = new JSpinner(new SpinnerNumberModel(00, 00, 23, 1));
		startTimeMM = new JSpinner(new SpinnerNumberModel(00, 00, 59, 1));
		endTimeHH = new JSpinner(new SpinnerNumberModel(00, 00, 23, 1));
		endTimeMM = new JSpinner(new SpinnerNumberModel(00, 00, 59, 1));
		vacationEvent = new JCheckBox("Vacation"); workEvent = new JCheckBox("Work"); meetingEvent = new JCheckBox("Meeting");
		familyEvent = new JCheckBox("Family"); schoolEvent = new JCheckBox("School");
	}
	public static void createAndShowGUI()
	{
		parentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		/* 
			---------------------------
			START of Menu Bar Configs.
			---------------------------
		*/
		
		// Exit functionality
		fileMenu.add(exit);
		exit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				parentFrame.dispose();
			}
		});
		
		// View Menu functionality
		viewMenu.add(dayView); viewMenu.add(monthView);
		viewGroup.add(dayView); viewGroup.add(monthView);
		monthView.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				statusLabel.setText("Clicked Month View.");
				isDayView = false;	// Setting isDayView to false as month view needs to be active upon selecting month view.
				contentAreaLabel.setText(monthViewTimeDisplay + dateFormatMonthView.format(currentDate));
			}
		});
		dayView.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				statusLabel.setText("Clicked Day View.");
				isDayView = true;	// Setting isDayView to true as day view needs to be active now upon selecting day view.
				contentAreaLabel.setText(dayViewTimeDisplay + dateFormatDayView.format(currentDate));
			}
		});
		menuBar.add(fileMenu); menuBar.add(viewMenu);
		/* 
			-------------------------
			END of Menu Bar Configs.
			-------------------------
		*/

		

		/* 
			-----------------------------
			START of Left Panel Configs.
			-----------------------------
		*/

		// Left Panel has 3 components - Today Panel, Travel Panel (which houses the previous and next buttons), and Add Appointment Panel.
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		todayPanel.add(today);
		travelPanel.setLayout(new FlowLayout());
		travelPanel.add(prev); travelPanel.add(next);
		addAppointmentButtonPanel.add(addAppointment);
		prev.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(isDayView)
				{
					statusLabel.setText("Clicked Previous Day");
					currentDate = currentDate.minusDays(1);
					contentAreaLabel.setText(dayViewTimeDisplay + dateFormatDayView.format(currentDate));
				}
				else
				{
					statusLabel.setText("Clicked Previous Month");
					currentDate = currentDate.minusMonths(1);
					contentAreaLabel.setText(monthViewTimeDisplay + dateFormatMonthView.format(currentDate));
				}
			}
		});
		next.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(isDayView)
				{
					statusLabel.setText("Clicked Next Day");
					currentDate = currentDate.plusDays(1);
					contentAreaLabel.setText(dayViewTimeDisplay + dateFormatDayView.format(currentDate));
				}
				else
				{
					statusLabel.setText("Clicked Next Month");
					currentDate = currentDate.plusMonths(1);
					contentAreaLabel.setText(monthViewTimeDisplay + dateFormatMonthView.format(currentDate));
				}
			}
		});
		today.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				statusLabel.setText("Clicked Today Button");
				currentDate = LocalDateTime.now();
				if(isDayView)
					contentAreaLabel.setText(dayViewTimeDisplay + dateFormatDayView.format(currentDate));
				else
					contentAreaLabel.setText(monthViewTimeDisplay + dateFormatMonthView.format(currentDate));
			}
		});
		leftPanel.add(todayPanel); leftPanel.add(travelPanel); leftPanel.add(addAppointmentButtonPanel);
		
		// Implementing a "sophisticated date and time picker" using JDatePicker (https://jdatepicker.org/about/) and the JSpinner Class.
		UtilDateModel model = new UtilDateModel();
		model.setSelected(true);
		Properties p = new Properties();
		p.put("text.today", "Today");
		p.put("text.month", "Month");
		p.put("text.year", "Year");
		JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
	    JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
	    addAppointmentPanel.setLayout(new BoxLayout(addAppointmentPanel, BoxLayout.PAGE_AXIS));
		startTimePanel.setLayout(new FlowLayout()); endTimePanel.setLayout(new FlowLayout()); appointmentTypePanel.setLayout(new FlowLayout());
		startTimePanel.add(dialogBoxStartTimeLabel); startTimePanel.add(startTimeHH); startTimePanel.add(new JLabel(":")); startTimePanel.add(startTimeMM); 
		endTimePanel.add(dialogBoxEndTimeLabel); endTimePanel.add(endTimeHH); endTimePanel.add(new JLabel(":")); endTimePanel.add(endTimeMM); 
		appointmentTypePanel.add(vacationEvent); appointmentTypePanel.add(workEvent); appointmentTypePanel.add(meetingEvent); 
		appointmentTypePanel.add(familyEvent); appointmentTypePanel.add(schoolEvent);
		
		// Setting up appointment panel
		addAppointmentPanel.add(appointmentName); addAppointmentPanel.add(datePicker); 
		addAppointmentPanel.add(startTimePanel); addAppointmentPanel.add(endTimePanel); addAppointmentPanel.add(appointmentTypePanel);

		// All functionalities within the dialog box is mentioned here
		addAppointment.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				statusLabel.setText("Add Appointment Clicked");
				// Set Default Event Name
				int result = JOptionPane.showConfirmDialog(null, addAppointmentPanel, "Add Appointment", JOptionPane.OK_CANCEL_OPTION);
				if (result == JOptionPane.OK_OPTION) {
					Date selectedDate = (Date) datePicker.getModel().getValue();
				    DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
				    String startTime = startTimeHH.getValue() + ":" + startTimeMM.getValue();
				    String endTime = endTimeHH.getValue() + ":" + endTimeMM.getValue();
				    String selectedTags = (vacationEvent.isSelected()? "| Vacation |" : "");
				    selectedTags += (workEvent.isSelected()? "| Work |" : "");
				    selectedTags += (meetingEvent.isSelected()? "| Meeting |" : "");
				    selectedTags += (familyEvent.isSelected()? "| Family |" : "");
				    selectedTags += (schoolEvent.isSelected()? "| School |" : "");
				    String appointmentReport = "Scheduled " + appointmentName.getText() + " on " + df.format(selectedDate) + " from " + startTime + " to "+ endTime+". \n";
				    if(selectedTags.length()>0)
				    	appointmentReport += "Selected Tags for this appointment: "+ selectedTags + ".";
				    statusLabel.setText(appointmentReport);
				}
				else
					statusLabel.setText("Cancelled creation of new appointment!");
			}
		});
		/* 
			---------------------------
			END of Left Panel Configs.
			---------------------------
		*/



		/*
			Status Panel Config
		*/
		statusPanel.setLayout(new FlowLayout());
		statusPanel.add(statusLabel);



		/* 
			Right Panel Configs.
		*/
		rightPanel.setLayout(new FlowLayout());
		rightPanel.add(contentAreaLabel);

		

		/* 
			START of Final placement of components onto parent frame.
		*/
		parentFrame.setJMenuBar(menuBar);
		parentFrame.setLayout(new BorderLayout());
		parentFrame.add(leftPanel, BorderLayout.WEST);
		parentFrame.add(statusLabel, BorderLayout.SOUTH);
		parentFrame.add(rightPanel, BorderLayout.CENTER);
		/* 
			END of Final placement of components onto parent frame.
		*/



		// Telling our calender app to assume required shape for accomodating all components, and then instructing it to display.
		parentFrame.setSize(900,600);
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
