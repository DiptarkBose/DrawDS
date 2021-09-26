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
import java.util.*;
import java.util.List;

class EventDetails
{
	String eventName;
	int startTimeHH, startTimeMM, endTimeHH, endTimeMM;
	public EventDetails(String n, int sh, int sm, int eh, int em)
	{
		eventName = n;
		startTimeHH = sh; startTimeMM = sm; endTimeHH = eh; endTimeMM = em;
	}
}

class DayView extends JComponent
{
	static DateTimeFormatter dateFormatDayView, dateFormatMonthView;
	static LocalDateTime currentDate;
	static boolean isDayView;
	static Map<String, List<EventDetails>> scheduleMap;
	static Map<Integer, Integer> hourPositionMap;
	static DateFormat df;

	public DayView()
	{
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		dateFormatDayView = DateTimeFormatter.ofPattern("MM/dd/yyyy"); 
		dateFormatMonthView = DateTimeFormatter.ofPattern("MM/yyyy");
		df = new SimpleDateFormat("MM/dd/yyyy");
		currentDate = LocalDateTime.now(); 
		isDayView = true;
		scheduleMap = new HashMap<>();
		hourPositionMap = new HashMap<>();
		int y = 100, i;
    	for(i=0; i<=23; i++, y+=50)
    		hourPositionMap.put(i, y);	
	}

	@Override
	public void paintComponent(Graphics graphics)
	{
    	Graphics2D g2d = (Graphics2D) graphics;
    	graphics.setColor(Color.WHITE);
    	graphics.fillRoundRect(10, 10, this.getWidth()-20, 10000, 30, 30);
    	graphics.setColor(Color.BLACK);
    	String displayDate = currentDate.getDayOfWeek().toString() + ", " + dateFormatDayView.format(currentDate).toString();
    	graphics.drawString(displayDate, 30, 50);
    	int y = 100, i;
    	for(i=0; i<=23; i++, y+=50)
    	{
    		graphics.drawString(i+":00", 30, y);
    		g2d.drawLine(70, y, this.getWidth()-50, y);
    	}
    	Date today = new Date();
    	today.setHours(0);
    	List<EventDetails> eventList = scheduleMap.getOrDefault(df.format(today).toString(), new ArrayList<>());
    	for(EventDetails evt : eventList)
    	{
    		int y1=hourPositionMap.get(evt.startTimeHH);
    		int y2=hourPositionMap.get(evt.endTimeHH);
    		addEvent(evt.eventName, y1, y2, graphics);
    	}
  	}
  	public void addEvent(String eventName, int y1, int y2, Graphics graphics)
  	{
  		graphics.setColor(Color.BLUE);
    	graphics.fillRoundRect(70, y1, 200, y2-y1, 10, 10);
    	graphics.setColor(Color.WHITE);
    	graphics.drawString(eventName, 95, (y1+y2)/2);
  	}
}

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
	static DayView dv;
	static Map<String, List<EventDetails>> scheduleMap;
	
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
		dv = new DayView();

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

				//Reset appointment values (Needed if user clicks Add Appointment multiple times)
				appointmentName.setText("New Appointment");
				startTimeHH.setValue(0); startTimeMM.setValue(0);
				endTimeHH.setValue(0); endTimeMM.setValue(0);
				vacationEvent.setSelected(false);
				workEvent.setSelected(false);
				meetingEvent.setSelected(false);
				familyEvent.setSelected(false);
				schoolEvent.setSelected(false);

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

				    // Create new event
				    String name = appointmentName.getText().toString();
				    Integer sh = (Integer)startTimeHH.getValue();
				    Integer sm = (Integer)startTimeMM.getValue();
				    Integer eh = (Integer)endTimeHH.getValue();
				    Integer em = (Integer)endTimeMM.getValue();
				    EventDetails evt = new EventDetails(name, sh, sm, eh, em);
				    List<EventDetails> eventList = scheduleMap.getOrDefault(df.format(selectedDate).toString(), new ArrayList<EventDetails>());
				    eventList.add(evt);
				    scheduleMap.put(df.format(selectedDate).toString(), eventList);
				    dv.scheduleMap = scheduleMap;
				    dv.repaint();
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
		//dv.setSize(300, 300);
		//rightPanel.setLayout(new FlowLayout());
		//rightPanel.add(dv);
		JScrollPane scroll = new JScrollPane(dv);

		

		/* 
			START of Final placement of components onto parent frame.
		*/
		
		parentFrame.setJMenuBar(menuBar);
		parentFrame.setLayout(new BorderLayout());
		parentFrame.add(leftPanel, BorderLayout.WEST);
		parentFrame.add(statusLabel, BorderLayout.SOUTH);
		parentFrame.add(scroll, BorderLayout.CENTER);
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
				scheduleMap = new HashMap<>();
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
