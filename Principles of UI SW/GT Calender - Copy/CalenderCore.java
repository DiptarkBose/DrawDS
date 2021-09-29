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
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

class EventDetails
{
	String eventName;
	int startTimeHH, startTimeMM, endTimeHH, endTimeMM, y1, y2;
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
	static Date curDate;
	static boolean isDayView;
	static Map<String, List<EventDetails>> scheduleMap;
	static Map<Integer, Integer> hourPositionMap, positionHourMap;
	static DateFormat df;
	int tentativeY1, tentativeY2;

	public DayView()
	{
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		dateFormatDayView = DateTimeFormatter.ofPattern("MM/dd/yyyy"); 
		dateFormatMonthView = DateTimeFormatter.ofPattern("MM/yyyy");
		df = new SimpleDateFormat("MM/dd/yyyy");
		currentDate = LocalDateTime.now(); 
		isDayView = true;
		scheduleMap = new HashMap<>();
		hourPositionMap = new HashMap<>(); positionHourMap = new HashMap<>();
		int y = 100, i;
    	for(i=0; i<=23; i++, y+=50)
    	{
    		hourPositionMap.put(i, y);
    		positionHourMap.put(y, i);
    	}
    	curDate = new Date();
    	curDate.setHours(0);
    	tentativeY1 = -1; tentativeY2 =-1;
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
    	
    	List<EventDetails> eventList = scheduleMap.getOrDefault(df.format(curDate).toString(), new ArrayList<>());
    	for(EventDetails evt : eventList)
    	{
    		int y1=hourPositionMap.get(evt.startTimeHH);
    		int y2=hourPositionMap.get(evt.endTimeHH);
    		evt.y1=y1; evt.y2=y2;
    		addEvent(evt.eventName, y1, y2, graphics);
    	}
    	if(tentativeY1!=-1 && tentativeY2!=-1)
    	{
    		int y1=tentativeY1;
    		int y2=tentativeY2;
    		Color lightBlue = new Color(75, 119, 190);
    		graphics.setColor(lightBlue);
    		if(y1<100) y1=100;
    		if(y2>2200) y2=2200;
	    	graphics.fillRoundRect(70, y1, 200, y2-y1, 10, 10);
	    	graphics.setColor(Color.WHITE);
	    	graphics.drawString("New Appointment", 95, (y1+y2)/2);
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

class DateUtil
{
    public static Date addDays(Date date, int days)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
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
	static DateFormat df;
	static UtilDateModel model;
	static Properties p;
	static JDatePanelImpl datePanel;
	static JDatePickerImpl datePicker;
	static boolean dragging, eventClicked;
	EventDetails selectedEvent;
	int rubberBandX1, rubberBandY1, rubberBandX2, rubberBandY2;

	public CalenderCore()
	{
		dragging = false;
		eventClicked = false;
		selectedEvent = null;

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
		df = new SimpleDateFormat("MM/dd/yyyy");
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

		// Content area variable
		contentAreaLabel = new JLabel(dayViewTimeDisplay + dateFormatDayView.format(currentDate));
		dv = new DayView();
		model = new UtilDateModel();
		model.setSelected(true);
		p = new Properties();
		p.put("text.today", "Today");
		p.put("text.month", "Month");
		p.put("text.year", "Year");
		datePanel = new JDatePanelImpl(model, p);
	    datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
	    addAppointmentPanel.setLayout(new BoxLayout(addAppointmentPanel, BoxLayout.PAGE_AXIS));
		startTimePanel.setLayout(new FlowLayout()); endTimePanel.setLayout(new FlowLayout()); appointmentTypePanel.setLayout(new FlowLayout());
		startTimePanel.add(dialogBoxStartTimeLabel); startTimePanel.add(startTimeHH); startTimePanel.add(new JLabel(":")); startTimePanel.add(startTimeMM); 
		endTimePanel.add(dialogBoxEndTimeLabel); endTimePanel.add(endTimeHH); endTimePanel.add(new JLabel(":")); endTimePanel.add(endTimeMM); 
		appointmentTypePanel.add(vacationEvent); appointmentTypePanel.add(workEvent); appointmentTypePanel.add(meetingEvent); 
		appointmentTypePanel.add(familyEvent); appointmentTypePanel.add(schoolEvent);
		
		// Setting up appointment panel
		addAppointmentPanel.add(appointmentName); addAppointmentPanel.add(datePicker); 
		addAppointmentPanel.add(startTimePanel); addAppointmentPanel.add(endTimePanel); addAppointmentPanel.add(appointmentTypePanel);
		dv.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                List<EventDetails> eventList = scheduleMap.getOrDefault(df.format(dv.curDate).toString(), new ArrayList<>());
                int x = e.getX(); int y = e.getY();
                selectedEvent = null;
                for(EventDetails evt : eventList)
                {
                	if(x>=70 && x<=270 && y>=evt.y1 && y<=evt.y2)
                	{
                		selectedEvent = evt;
                		break;
                	}
                }
                if(selectedEvent!=null)
                {
                	eventClicked = true;
                	if(e.getClickCount()==2)
                	{
                		appointmentName.setText(selectedEvent.eventName);
						startTimeHH.setValue(selectedEvent.startTimeHH); startTimeMM.setValue(selectedEvent.startTimeMM);
						endTimeHH.setValue(selectedEvent.endTimeHH); endTimeMM.setValue(selectedEvent.endTimeMM);
						vacationEvent.setSelected(false);
						workEvent.setSelected(false);
						meetingEvent.setSelected(false);
						familyEvent.setSelected(false);
						schoolEvent.setSelected(false);

						int result = JOptionPane.showConfirmDialog(null, addAppointmentPanel, "Edit Appointment", JOptionPane.OK_CANCEL_OPTION);
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
						    String appointmentReport = "Re-scheduled " + appointmentName.getText() + " on " + df.format(selectedDate) + " from " + startTime + " to "+ endTime+". \n";
						    if(selectedTags.length()>0)
						    	appointmentReport += "Selected Tags for this appointment: "+ selectedTags + ".";
						    statusLabel.setText(appointmentReport);

						    // Create new event
						    String name = appointmentName.getText().toString();
						    Integer sh = (Integer)startTimeHH.getValue();
						    Integer sm = (Integer)startTimeMM.getValue();
						    Integer eh = (Integer)endTimeHH.getValue();
						    Integer em = (Integer)endTimeMM.getValue();
						    selectedEvent.eventName = name; selectedEvent.startTimeHH = sh; selectedEvent.startTimeMM = sm; selectedEvent.endTimeHH = eh; selectedEvent.endTimeMM = em;
						    eventList.remove(selectedEvent);
						    List<EventDetails> newEventList = scheduleMap.getOrDefault(df.format(selectedDate).toString(), new ArrayList<EventDetails>());
						    newEventList.add(selectedEvent);
						    scheduleMap.put(df.format(selectedDate).toString(), newEventList);
						    dv.scheduleMap = scheduleMap;
						    dv.repaint();
						}
						else
							statusLabel.setText("Cancelled edit of new appointment!");
                	}
                }
                else
                {
                	eventClicked = false;
                	if(e.getClickCount()==2)
                	{
                		int y1 = e.getY();
                		if(y1>=100)
                		{
	                		appointmentName.setText("New Appointment");
	                		while(y1>=100)
	                		{
	                			if(dv.positionHourMap.containsKey(y1)) break;
	                			y1--;
	                		}
							startTimeHH.setValue(dv.positionHourMap.get(y1)); startTimeMM.setValue(0);
							endTimeHH.setValue(dv.positionHourMap.get(y1+50)); endTimeMM.setValue(0);
							vacationEvent.setSelected(false);
							workEvent.setSelected(false);
							meetingEvent.setSelected(false);
							familyEvent.setSelected(false);
							schoolEvent.setSelected(false);

							int result = JOptionPane.showConfirmDialog(null, addAppointmentPanel, "Edit Appointment", JOptionPane.OK_CANCEL_OPTION);
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
							    String appointmentReport = "Re-scheduled " + appointmentName.getText() + " on " + df.format(selectedDate) + " from " + startTime + " to "+ endTime+". \n";
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
							    List<EventDetails> newEventList = scheduleMap.getOrDefault(df.format(selectedDate).toString(), new ArrayList<EventDetails>());
							    newEventList.add(evt);
							    scheduleMap.put(df.format(selectedDate).toString(), newEventList);
							    dv.scheduleMap = scheduleMap;
							    dv.repaint();
							}
							else
								statusLabel.setText("Cancelled addition of new appointment!");
						}
                	}
                }
            }
            @Override
		    public void mouseReleased(MouseEvent event) {
		    	dragging = false;
		    	
		    	if(eventClicked)
		    	{
		    		int newY = event.getY(); int closestY1;
		    		if(newY<100) closestY1 = 100;
		    		else
		    		{
		    			closestY1 = newY;
		    			while(closestY1>=100)
		    			{
		    				if(dv.positionHourMap.containsKey(closestY1)) break;
		    				closestY1--;
		    			}
		    		}
		    		int height = selectedEvent.y2-selectedEvent.y1;
		    		selectedEvent.y1 = closestY1;
		    		selectedEvent.y2 = closestY1+height;
		    		selectedEvent.startTimeHH = dv.positionHourMap.get(selectedEvent.y1);
		    		selectedEvent.endTimeHH = dv.positionHourMap.get(selectedEvent.y2);
		    		dv.scheduleMap = scheduleMap;
		    		statusLabel.setText("Rescheduled "+selectedEvent.eventName+": "+selectedEvent.startTimeHH+":00 to "+ selectedEvent.endTimeHH+":00.");
		    		dv.repaint();
		    	}
		    	else
		    	{
		    		int newY1 = dv.tentativeY1; int newY2 = dv.tentativeY2;
		    		if(newY1<100) newY1 = 100;
		    		else
		    		{
		    			while(newY1>=100)
		    			{
		    				if(dv.positionHourMap.containsKey(newY1)) break;
		    				newY1--;
		    			}
		    		}
		    		if(newY2>2200) newY2 = 2200;
		    		else
		    		{
		    			while(newY2<=2200)
		    			{
		    				if(dv.positionHourMap.containsKey(newY2)) break;
		    				newY2++;
		    			}
		    		}
		    		String name = "New Appointment";
				    Integer sh = dv.positionHourMap.get(newY1);
				    Integer sm = 0;
				    Integer eh = dv.positionHourMap.get(newY2);
				    Integer em = 0;
		    		EventDetails draggedEvent = new EventDetails(name, sh, sm, eh, em);
		    		List<EventDetails> newEventList = scheduleMap.getOrDefault(df.format(dv.curDate).toString(), new ArrayList<EventDetails>());
					newEventList.add(draggedEvent);
					scheduleMap.put(df.format(dv.curDate).toString(), newEventList);
					dv.scheduleMap = scheduleMap;
							    
		    		dv.tentativeY1 = -1; dv.tentativeY2 = -1;
		    		dv.repaint();
		    	}
		    }
		    @Override
    		public void mousePressed(MouseEvent e) {
    			dragging = true;
    			selectedEvent = null;
    			List<EventDetails> eventList = scheduleMap.getOrDefault(df.format(dv.curDate).toString(), new ArrayList<>());
                int x = e.getX(); int y = e.getY();
                for(EventDetails evt : eventList)
                {
                	if(x>=70 && x<=270 && y>=evt.y1 && y<=evt.y2)
                	{
                		selectedEvent = evt;
                		break;
                	}
                }
                if(selectedEvent!=null) eventClicked = true;
                else 
                {
                	eventClicked = false;
                	dv.tentativeY1 = e.getY();
                }
    		}
		    
        });
		dv.addMouseMotionListener(new MouseAdapter() {
            @Override
		    public void mouseDragged(MouseEvent event) {
		    	if(eventClicked)
		    	{
		    		int newY = event.getY(); int closestY1;
		    		if(newY<100) closestY1 = 100;
		    		else
		    		{
		    			closestY1 = newY;
		    			while(closestY1>=100)
		    			{
		    				if(dv.positionHourMap.containsKey(closestY1)) break;
		    				closestY1--;
		    			}
		    		}
		    		int height = selectedEvent.y2-selectedEvent.y1;
		    		selectedEvent.y1 = closestY1;
		    		selectedEvent.y2 = closestY1+height;
		    		selectedEvent.startTimeHH = dv.positionHourMap.get(selectedEvent.y1);
		    		selectedEvent.endTimeHH = dv.positionHourMap.get(selectedEvent.y2);
		    		dv.scheduleMap = scheduleMap;
		    		dv.repaint();
		    	}
		    	else
		    	{
		    		dv.tentativeY2 = event.getY();
		    		dv.repaint();
		    	}
		    }
        });
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
				dv.currentDate = currentDate;
				Calendar cal = Calendar.getInstance(); 
				cal.setTime(dv.curDate); 
				cal.add(Calendar.DATE, -1);
				dv.curDate = cal.getTime();
				dv.repaint();
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
				dv.currentDate = currentDate;
				Calendar cal = Calendar.getInstance(); 
				cal.setTime(dv.curDate); 
				cal.add(Calendar.DATE, 1);
				dv.curDate = cal.getTime();
				dv.repaint();
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
				dv.currentDate = currentDate;
				Date d = new Date();
    			d.setHours(0);
				dv.curDate = d;
				dv.repaint();
			}
		});
		leftPanel.add(todayPanel); leftPanel.add(travelPanel); leftPanel.add(addAppointmentButtonPanel);
		
		// Implementing a "sophisticated date and time picker" using JDatePicker (https://jdatepicker.org/about/) and the JSpinner Class.
		
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
