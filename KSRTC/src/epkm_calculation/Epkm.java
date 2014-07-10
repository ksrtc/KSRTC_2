package epkm_calculation;

import javax.xml.parsers.*;

import org.w3c.dom.*; 

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;

class TimeZone
{
	float epm[][];
	int start, end, busCount[][];
	
	public TimeZone(int start, int end)
	{
		epm = new float[1000][1000];
		busCount = new int[1000][1000];
		this.start = start;
		this.end = end;
	}

	public int getStart() 
	{
		return start;
	}

	public int getEnd() 
	{
		return end;
	}

	public float getEpm(int i, int j) 
	{
		return epm[i][j];
	}

	public void setEpm(int i, int j, float epm) 
	{
		this.epm[i][j] = (this.epm[i][j] == 0) ? epm : (this.epm[i][j] + epm) / 2.0f;
		this.busCount[i][j]++;
	}
	
	public int getBusCount(int i, int j) 
	{
		return busCount[i][j];
	}
}

class Bus
{
	String id, schedule;
	float epkm;
	
	public Bus() 
	{
		id = "";
		schedule = "";
		epkm = 0.0f;
	}

	public String getId() 
	{
		return id;
	}
	
	public void setId(String id) 
	{
		this.id = id;
	}
	
	public String getSchedule() 
	{
		return schedule;
	}
	
	public void setSchedule(String schedule) 
	{
		this.schedule = schedule;
	}
	
	public float getEpkm() 
	{
		return epkm;
	}
	
	public void setEpkm(float epkm) 
	{
		this.epkm = epkm;
	}
}

public class Epkm 
{
	TimeZone[] T;
	ArrayList<Bus> BusInfo;
	float[][] epm;
	BufferedWriter bwTimeZones, bwBus;
	String[] filePathEpkm = {
								"epkmScheduleData\\BNG_epkm\\Bangalore EPKM 2011-12_output.xml",
								"epkmScheduleData\\DVG_epkm\\BDT_epkm_4_output.xml",
								"epkmScheduleData\\DVG_epkm\\CDG_epkm_6_output.xml",
								"epkmScheduleData\\DVG_epkm\\DVG_epkm_1_output.xml",
								"epkmScheduleData\\DVG_epkm\\HRP_epkm_5_output.xml",
								"epkmScheduleData\\DVG_epkm\\HRR_epkm_3_output.xml",
								"epkmScheduleData\\DVG_epkm\\SGR_epkm_3_output.xml",
								"epkmScheduleData\\DVG_epkm\\SMG_epkm_2_output.xml",
								"epkmScheduleData\\HSN_epkm\\HSN_epkm_output.xml",
								"epkmScheduleData\\KLR_epkm\\KGF_epkm_output.xml",
								"epkmScheduleData\\KLR_epkm\\KLR_epkm_output.xml",
								"epkmScheduleData\\KLR_epkm\\MBL_epkm_output.xml",
								"epkmScheduleData\\KLR_epkm\\MLR_epkm_output.xml",
								"epkmScheduleData\\KLR_epkm\\SVP_epkm_output.xml",
								"epkmScheduleData\\MDY_epkm\\KRP_epkm_5_output.xml",
								"epkmScheduleData\\MDY_epkm\\MDR_epkm_1_output.xml",
								"epkmScheduleData\\MDY_epkm\\MDY_epkm_2_output.xml",
								"epkmScheduleData\\MDY_epkm\\MLV_epkm_3_output.xml",
								"epkmScheduleData\\MDY_epkm\\NGM_epkm_4_output.xml",
								"epkmScheduleData\\MDY_epkm\\PDP_epkm_6_output.xml",
								"epkmScheduleData\\MYS_epkm\\MYS_epkm_output.xml"
							};
	String[] filePathSchedule = {
									"epkmScheduleData\\BNG_epkm\\Bangalore SCHEDULE 2011-12_output.xml",
									"epkmScheduleData\\DVG_epkm\\BDT_output.xml",
									"epkmScheduleData\\DVG_epkm\\CDG_output.xml",
									"epkmScheduleData\\DVG_epkm\\DVG_output.xml",
									"epkmScheduleData\\DVG_epkm\\HRP_output.xml",
									"epkmScheduleData\\DVG_epkm\\HRR_output.xml",
									"epkmScheduleData\\DVG_epkm\\SGR_output.xml",
									"epkmScheduleData\\DVG_epkm\\SMG_output.xml",
									"epkmScheduleData\\HSN_epkm\\HSN_output.xml",
									"epkmScheduleData\\KLR_epkm\\KGF_output.xml",
									"epkmScheduleData\\KLR_epkm\\KLR_output.xml",
									"epkmScheduleData\\KLR_epkm\\MBL_output.xml",
									"epkmScheduleData\\KLR_epkm\\MLR_output.xml",
									"epkmScheduleData\\KLR_epkm\\SVP_output.xml",
									"epkmScheduleData\\MDY_epkm\\KRP_output.xml",
									"epkmScheduleData\\MDY_epkm\\MDR_output.xml",
									"epkmScheduleData\\MDY_epkm\\MDY_output.xml",
									"epkmScheduleData\\MDY_epkm\\MLV_output.xml",
									"epkmScheduleData\\MDY_epkm\\NGM_output.xml",
									"epkmScheduleData\\MDY_epkm\\PDP_output.xml",
									"epkmScheduleData\\MYS_epkm\\MYS_schedule_output.xml"
								};

	ArrayList<String> station;
	Document docEpkm,docSchedule;
	DataInputStream in;

	public Epkm() throws Exception 
	{
		station = new ArrayList<String>();
		BusInfo = new ArrayList<Bus>();
		epm = new float[1000][1000];
		T = new TimeZone[6];
		//0-6, 6-9, 9-12, 12-16, 16-20, 20-24
		T[0] = new TimeZone(0, 6);
		T[1] = new TimeZone(6, 9);
		T[2] = new TimeZone(9, 12);
		T[3] = new TimeZone(12, 16);
		T[4] = new TimeZone(16, 20);
		T[5] = new TimeZone(20, 24);
		in = new DataInputStream(System.in);
		bwTimeZones = new BufferedWriter(new FileWriter(new File("Output\\Epkm with Time Zones.txt")));
		bwBus = new BufferedWriter(new FileWriter(new File("Output\\Epkm with Bus Schedules.txt")));
		for(int i = 0; i < filePathEpkm.length; i++)
		{
			docEpkm = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(filePathEpkm[i]));
			docEpkm.getDocumentElement().normalize();
			docSchedule = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(filePathSchedule[i]));
			docSchedule.getDocumentElement().normalize();
			epm_calculator(docEpkm.getElementsByTagName("schedule"), docSchedule.getElementsByTagName("schedule"));
		}
		System.out.println("Unique Stations: " + station.size());
		showTimeZones();
		showBus();
		System.out.print("Data Written Successfully !!");
		/*
		String choice;
		do
		{
			getNewData();
			System.out.print("\nContinue (y/n): ");
			choice = in.readLine();
		}
		while(!choice.equalsIgnoreCase("n"));
		*/
	}
	
	public void epm_calculator(NodeList nListEpkm, NodeList nListSchedule) 
	{
		Element eElementEpkm = null;
		Element eElementSchedule = null;
		int start, end;
		float lepkm, deptTime, time[];
		String start_station, end_station, schedule[];
		float a, b;
		for (int i = 0; i < nListEpkm.getLength(); i++) 
		{
			eElementEpkm = (Element) nListEpkm.item(i);
			a = Float.valueOf(eElementEpkm.getElementsByTagName("revenue").item(0).getTextContent().trim()).floatValue();
			b = Float.valueOf(eElementEpkm.getElementsByTagName("effkms").item(0).getTextContent().trim()).floatValue();
			lepkm = (a > b) ? (a / b) : (b / a);
			for(int j = 0; j < nListSchedule.getLength(); j++)
			{
				eElementSchedule = (Element) nListSchedule.item(j);
				if(eElementEpkm.getElementsByTagName("id").item(0).getTextContent().trim().replaceAll("[^\\w]","").equals(eElementSchedule.getElementsByTagName("id").item(0).getTextContent().trim().replaceAll("[^\\w]","")))
				{
					Bus B = new Bus();
					B.setId(eElementSchedule.getElementsByTagName("id").item(0).getTextContent().trim().replaceAll("[^\\w]",""));
					if(eElementSchedule.getElementsByTagName("start_station").item(0) != null && eElementSchedule.getElementsByTagName("trip").item(0) == null)
					{
						start_station = eElementSchedule.getElementsByTagName("start_station").item(0).getTextContent().trim();
						end_station = eElementSchedule.getElementsByTagName("end_station").item(0).getTextContent().trim();
						B.setSchedule(start_station + " - " + end_station);
						deptTime = Float.valueOf(eElementSchedule.getElementsByTagName("dept_hr").item(0).getTextContent().trim()).floatValue();
						if(deptTime > 24)
							deptTime = Float.valueOf(eElementSchedule.getElementsByTagName("dept_min").item(0).getTextContent().trim()).floatValue() + deptTime / 100;
						else
							deptTime += Float.valueOf(eElementSchedule.getElementsByTagName("dept_min").item(0).getTextContent().trim()).floatValue() / 100;
						if (!station.contains(start_station)) 
							station.add(start_station);
						if (!station.contains(end_station)) 
							station.add(end_station);
						start = station.indexOf(start_station);
						end = station.indexOf(end_station);
						epm[start][end] = (epm[start][end] == 0) ? lepkm : (epm[start][end] + lepkm) / 2.0f;
						B.setEpkm(epm[start][end]);
						BusInfo.add(B);
						for(int t = 0; t < T.length; t++)
							if(deptTime >= T[t].getStart() && deptTime < T[t].getEnd())
								T[t].setEpm(start, end, lepkm);
						break;
					}
					schedule = new String[eElementSchedule.getElementsByTagName("trip").getLength() + 1];
					time = new float[eElementSchedule.getElementsByTagName("trip").getLength()];
					for (int k = 0; k < (schedule.length - 1); k++) 
					{
						schedule[k] = ((Element)eElementSchedule.getElementsByTagName("trip").item(k)).getElementsByTagName("start_station").item(0).getTextContent().trim();
						if (!station.contains(schedule[k])) 
							station.add(schedule[k]);
						if(k == (schedule.length - 2))
						{
							schedule[k + 1] = ((Element)eElementSchedule.getElementsByTagName("trip").item(k)).getElementsByTagName("end_station").item(0).getTextContent().trim();;
							if (!station.contains(schedule[k + 1])) 
								station.add(schedule[k + 1]);
						}
						time[k] = Float.valueOf(((Element)eElementSchedule.getElementsByTagName("trip").item(k)).getElementsByTagName("dept_hr").item(0).getTextContent().trim()).floatValue();
						if(time[k] > 24)
							time[k] = Float.valueOf(((Element)eElementSchedule.getElementsByTagName("trip").item(k)).getElementsByTagName("dept_min").item(0).getTextContent().trim()).floatValue() + time[k] / 100;
						else
							time[k] += Float.valueOf(((Element)eElementSchedule.getElementsByTagName("trip").item(k)).getElementsByTagName("dept_min").item(0).getTextContent().trim()).floatValue() / 100;
					}
					String sch = "";
					for(int p = 0; p < schedule.length; p++)
						sch = sch + schedule[p] + " - ";
					B.setSchedule(sch.substring(0, sch.lastIndexOf(" - ")));
					for(int p = 0; p < schedule.length - 1; p++)
						for(int q = (p + 1); q < schedule.length; q++)
						{
							start = station.indexOf(schedule[p]);
							end = station.indexOf(schedule[q]);
							if(start != end)
							{
								epm[start][end] = (epm[start][end] == 0) ? lepkm : (epm[start][end] + lepkm) / 2.0f;
								B.setEpkm(epm[start][end]);
								for(int t = 0; t < T.length; t++)
									if(time[p] >= T[t].getStart() && time[p] < T[t].getEnd())
										T[t].setEpm(start, end, lepkm);
							}
						}
					BusInfo.add(B);
					break;
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public void getData() throws IOException
	{
		System.out.println("\nEnter Schedule with Station Codes seperated with '-' in caps: ");
		String sch = in.readLine();
		String[] schedule = sch.split("-");
		float sepkm = 0;
		int x = 0, src, dest;
		outer: for(int i = 0; i < schedule.length - 1; i++)
			for(int j = (i + 1); j < schedule.length; j++)
			{
				src = station.indexOf(schedule[i]);
				dest = station.indexOf(schedule[j]);
				if(src == -1)
					continue outer;
				if(dest == -1)
					continue;
				if(epm[src][dest] != 0)
				{
					x++;
					sepkm += epm[src][dest];
				}
				else
					if(epm[dest][src] != 0)
					{
						x++;
						sepkm += epm[dest][src];
					}
			}
		System.out.println("\nEPKM: " + (x == 0 ? "Not Available" : new DecimalFormat("#.##").format(sepkm / x)));
	}
	
	@SuppressWarnings("deprecation")
	public void getNewData() throws Exception
	{
		System.out.print("\nEnter Schedule with Station Codes seperated with '-' in caps: ");
		String sch = in.readLine();
		System.out.print("\nEnter Time of Commute: ");
		float time = Float.valueOf(in.readLine()).floatValue();
		time = time % 24;
		String[] schedule = sch.split("-");
		int src = station.indexOf(schedule[0]);
		int dest = station.indexOf(schedule[1]);
		if(src == -1 || dest == -1)
		{
			System.out.println("\nNo such Stations!");
			return;
		}
		for(int i = 0; i < T.length; i++)
			if(time >= T[i].getStart() && time < T[i].getEnd())
			{
				System.out.println("\nEPKM: " + new DecimalFormat("#.##").format(T[i].getEpm(station.indexOf(schedule[0]), station.indexOf(schedule[1]))));
				break;
			}
	}
	
	public void showTimeZones() throws Exception
	{
		//System.out.println("\nSERIAL\tSOURCE\tDESTINATION\tTIMEZONE\t\tEPKM\n");
		int count = 1;
		for(int src = 0; src < station.size(); src++)
			for(int dest = 0; dest < station.size(); dest++)
				if(src != dest)
					for(int k = 0; k < T.length; k++)
						if(T[k].getEpm(src, dest) != 0)
						{
							//System.out.print("\n" + (count++) + "\t" + station.get(src) + "\t" + station.get(dest) + "\t" + T[k].start + " - " + T[k].end + "\t\t" + new DecimalFormat("#.##").format(T[k].getEpm(src, dest)));
							bwTimeZones.newLine();
							bwTimeZones.append("SERIAL: " + (count++));
							bwTimeZones.newLine();
							bwTimeZones.append("SOURCE: " + station.get(src));
							bwTimeZones.newLine();
							bwTimeZones.append("DESTINATION: " + station.get(dest));
							bwTimeZones.newLine();
							bwTimeZones.append("TIMEZONE: " + T[k].start + " - " + T[k].end);
							bwTimeZones.newLine();
							bwTimeZones.append("BUSES VISITED: " + T[k].getBusCount(src, dest));
							bwTimeZones.newLine();
							bwTimeZones.append("EPKM: " + new DecimalFormat("#.##").format(T[k].getEpm(src, dest)));
							bwTimeZones.newLine();
						}
		//System.out.println();
		bwTimeZones.flush();
		bwTimeZones.close();
	}
	
	public void showBus() throws Exception
	{
		int count = 1;
		for(int i = 0; i < BusInfo.size(); i++)
		{
			bwBus.newLine();
			bwBus.append("SERIAL: " + (count++));
			//System.out.print("\nBUS ID: " + BusInfo.get(i).getId());
			bwBus.newLine();
			bwBus.append("BUS ID: " + BusInfo.get(i).getId());
			//System.out.print("\nSCHEDULE: " + BusInfo.get(i).getSchedule());
			bwBus.newLine();
			bwBus.append("SCHEDULE: " + BusInfo.get(i).getSchedule());
			//System.out.println("\nEPKM: " + BusInfo.get(i).getEpkm());
			bwBus.newLine();
			bwBus.append("EPKM: " + BusInfo.get(i).getEpkm());
			bwBus.newLine();
		}
		//System.out.println();
		bwBus.flush();
		bwBus.close();
	}
	
	public static void main(String argv[]) 
	{
		try 
		{
			new Epkm();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
}
