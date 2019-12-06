import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

class ClientProgram{
	
    private static String clientPath = new String();
    
    
    private static String managerPath = new String();
    private static String commandFilePath = new String();
    private static String parametersFilePath = new String();
    private static String jobPath = new String();
    private static String managerName = new String();
    private static String queuePath= new String();
    private static String jobName= new String();

    private static List<String> baseCommand;  //contains py(first command) and sum.py(executableFileName)
    private static String firstCommand= new String();
    private static String executableFileName= new String();
    private static File queuePathFolder;

    
    private static String executableFilePath= new String();
    
    private static String doneJobPath = new String();

    
    private static ArrayList<File> managerList;
    private static  ArrayList<File> jobList;
    
    private static 	List<String> parametersList;

    private static String clientNum;

    
    private static String command = new String();
    
    private static String baseStoragePath;
    private static String baseClientDirName = "\\Client";
    private static String commandFileName = "command.txt";
    private static String parametersFileName = "parameters.txt";
    private static StringBuffer output = new StringBuffer();
    
    private static File clientPathFolder;
    
    private static String outputFileName="output.txt";
    private static String outputFolderName="Outputs";
    private static String outputFolderPath;
    private static String outputFilePath;
    
    private static String osName=System.getProperty("os.name");
    
    private static String jobType = new String();
    
    
    
    //for Archivable job types
    private static String folderPathToMakeArchive;
    private static String folderNameToMakeArchive;

    private static List <String> archiverFileList=new ArrayList < String > ();

    private static String outputZipFile;

    private static String[] managerAndJobNameAndType;
    
    
    
    public static String runCommand(String command) {
    	
		//And don't forget, if you are running in Windows, you need to put "cmd /c " in front of your command.
    	Process p;
		try {
			p = Runtime.getRuntime().exec(command);
		
    		
			Thread a = new Thread(new Runnable() {
			    public void run() {
			     BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			     String line = null; 
			     try {
			        while ((line = input.readLine()) != null) {
			        	output.append(line);

			        }
			     } catch (IOException e) {
			            e.printStackTrace();
			     }
			    }
			});

			a.start();
			a.join(); //why?
			p.waitFor(); //why?

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 catch (IOException e1) {
			    if(osName.charAt(0)=='W') {
			    	System.err.println(e1.getMessage());   
			        System.exit(0);
			    }
			    else {
			    	//download the program with command line
			    }
		}
		return output.toString();
    	
    }
    public static void main(String[] args) throws InterruptedException{
    	
    	clientNum=args[0];
		
		baseStoragePath=args[1];
		
		baseStoragePath=baseStoragePath.replace("/", "\\");
		baseStoragePath=baseStoragePath.replace("//", "\\");
		baseStoragePath=baseStoragePath.replace("\\\\", "\\");
		baseStoragePath=baseStoragePath.replace("\\\\\\", "\\");
		baseStoragePath=baseStoragePath.replace("/", "\\");
		baseStoragePath=baseStoragePath.replace("//", "\\");
        
		baseStoragePath = "\\"+baseStoragePath;

		
		
		
		
		
		

		int port = Integer.parseInt("8888");
 
		try (ServerSocket serverSocket = new ServerSocket(port)) {
 
		    System.out.println("Server is listening on port " + port);
 
		    while (true) {
		        Socket socket = serverSocket.accept();
		        System.out.println("New client connected");
 
		        InputStream input = socket.getInputStream();
		        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		        
		        OutputStream outputStream = socket.getOutputStream();
		        PrintWriter writer = new PrintWriter(outputStream, true);
 

		        String text;
 
		        while(true) {
		        	 text = reader.readLine();
			            
		        	 
		        	 //Sample text request from Server for Archiver Job = "Manager-burhan|Job-teneme|Archiver|maindir1"
		        	 //Sample text request from Server for Executable Job = "Manager-burhan|Job-ghhghj|Executable"
		        	 System.out.println("TEXT:"+text);
			            
			            
			            if(text.equals("bye")) {
					        socket.close();
			            	break;
			            }
			            
			            
			            
			            managerAndJobNameAndType = text.split("[|]");
			            
			            System.out.println("texttexttext:"+text);
			            
			            managerName=managerAndJobNameAndType[0];
			            jobName=managerAndJobNameAndType[1];
			            jobType=managerAndJobNameAndType[2];

			            System.out.println("Manager name:"+ managerName);

			            System.out.println("Job name:"+ jobName);

			            System.out.println("Job type:"+ jobType);

						//default for all jobs
				    	initClientPath();
						initManagerPath();
						initQueuePath();
						initJobPath();
						
						
						if(jobType.equals("Executable")) {
							//specific for Executable job
							initCommandFilePath();
							initParametersList();			
							initCommand();
							initDoneJobPath();
							runCommand(command);
							
							createOutputFolder();
							writeOutputToFile();
							
							moveJobFromQueueToDoneFolder();

							System.out.println("COMMAND:"+command);
							System.out.println("OUTPUT:"+output);
				            //writer.println("output:" + output);
				            System.out.println("Job name:"+ jobName);

				            String messageString = doneJobPath+"*"+jobName+"*"+managerName;
				            
				            messageString=messageString.replace("/", "\\");
				            messageString=messageString.replace("//", "\\");
				            messageString=messageString.replace("\\\\", "\\");
				            messageString=messageString.replace("\\\\\\", "\\");
				            messageString=messageString.replace("/", "\\");
				            messageString=messageString.replace("//", "\\");
				            
				            
							writer.println(messageString);



							
							
						}
						else if(jobType.equals("Archiver")) {
							//specific for Archiver job
							
							initFolderNameToMakeArchive();
							initFolderPathToMakeArchive();
				            
							initDoneJobPath();
				            createOutputFolder();
				            
				            initOutputZipFile();

				            zipTheFolder();
				            
				            moveJobFromQueueToDoneFolder();
				            
				            
				            String messageString = doneJobPath+"*"+jobName+"*"+managerName;

				            messageString=messageString.replace("/", "\\");
				            messageString=messageString.replace("//", "\\");
				            messageString=messageString.replace("\\\\", "\\");
				            messageString=messageString.replace("\\\\\\", "\\");
				            messageString=messageString.replace("/", "\\");
				            messageString=messageString.replace("//", "\\");
				            messageString = "\\"+messageString;


							writer.println(messageString);
							

							archiverFileList.clear();
							

						}

						destructStrings();

						destructStrings2();

			            
		           
	 
		        }

		           
		        
 
		    }
 
		} catch (IOException ex) {
		    System.out.println("Server exception: " + ex.getMessage());
		    ex.printStackTrace();
		}

    	
    }
    

	    	
	    	
	    	
	    	
	    	
	
    

	private static void zipTheFolder() {
        generateFileList(new File(folderPathToMakeArchive));
        zipIt(outputZipFile);		
	}
	private static void initOutputZipFile() {
        outputZipFile=outputFolderPath+"/"+folderNameToMakeArchive+".zip";		
	}
	private static void initFolderPathToMakeArchive() {
        folderPathToMakeArchive= jobPath+"/"+folderNameToMakeArchive;		
	}
	private static void initFolderNameToMakeArchive() {
        folderNameToMakeArchive=managerAndJobNameAndType[3];
		
	}
	private static void initOutputFilePath() {
		outputFilePath=jobPath+"/"+outputFolderName+"/"+outputFileName;
	}
	private static void initOutputFolderPath() {
		outputFolderPath=jobPath+"/"+outputFolderName;
		initOutputFilePath();
	}
	private static void createOutputFolder() {
		initOutputFolderPath();				
		new File(outputFolderPath).mkdirs();
		
	}
	private static void writeOutputToFile() throws FileNotFoundException {
		PrintWriter out = new PrintWriter(outputFilePath);
		out.println(output);
		out.close();
	}
	private static void initJobList() {
		System.out.println("queuePathFolder:"+queuePathFolder);
   		jobList = new ArrayList<File>(Arrays.asList(queuePathFolder.listFiles()));		
	}
	private static void initManagerList() {
		System.out.println("clientPathFolder:"+clientPathFolder);
		managerList = new ArrayList<File>(Arrays.asList(clientPathFolder.listFiles()));		
	}
	private static void initClientPath() {
    	clientPath=baseStoragePath+baseClientDirName+clientNum;		
    	clientPathFolder= new File(clientPath);
	}
	private static void destructStrings() {
	    commandFilePath = new String();
	    parametersFilePath = new String();
	    jobPath = new String();
	    queuePath=new String();
	    managerPath=new String();
	    doneJobPath=new String();
	    command = new String();
	    firstCommand = new String();
	    executableFilePath= new String();
	    executableFileName= new String();
	    output=new StringBuffer();
	    outputFolderPath=new String();
	    outputFilePath=new String();

		
	}
	private static void destructStrings2() {
	    commandFilePath = new String();
	    parametersFilePath = new String();
	    jobPath = new String();
	    doneJobPath=new String();
	    command = new String();
	    firstCommand = new String();
	    executableFilePath= new String();
	    executableFileName= new String();
	    output=new StringBuffer();
	    outputFolderPath=new String();
	    outputFilePath=new String();

	
}
	private static void initDoneJobPath() {
    	doneJobPath=managerPath+"done/"+jobName;
		
	}
	private static void initCommand() throws IOException {
		initFirstCommand();
		initExecutableFileName();
		initExecutableFilePath();
		command+=firstCommand;
		command+=" ";
		command+=executableFilePath;
		
		for(int k=0;k<parametersList.size();k++) {
			command = command+ " "+parametersList.get(k);
		}
	}
	private static void initParametersList() throws IOException {
		initParametersFilePath();
    	parametersList=Files.readAllLines(Paths.get(parametersFilePath), StandardCharsets.US_ASCII);		
	}
	private static void initExecutableFilePath() {
		executableFilePath=jobPath+"/"+executableFileName;		
	}
	private static void initExecutableFileName() {
		executableFileName=baseCommand.get(1);
		executableFileName=executableFileName.replace("\n", "");
	}
	private static void initFirstCommand() throws IOException {
		baseCommand = Files.readAllLines(Paths.get(commandFilePath), StandardCharsets.US_ASCII);	
    	firstCommand=baseCommand.get(0);
    	firstCommand=firstCommand.replace("\n", "");	
   }

	private static void initParametersFilePath() {
		parametersFilePath=jobPath;
		parametersFilePath+="/";
		parametersFilePath+=parametersFileName;
	}
	private static void initCommandFilePath() {
		commandFilePath=jobPath;
		commandFilePath+="/";
		commandFilePath+=commandFileName;		
	}
	private static void initJobPath() {
		jobPath+=queuePath;
		jobPath+=jobName;
	}
	private static void initQueuePath() {
    	queuePath+=managerPath;
		queuePath+="queue/";		
		queuePathFolder = new File(queuePath);

	}
	private static void initManagerPath() {
		managerPath+=clientPath;
		managerPath+="/";
		managerPath+=managerName;
		managerPath+="/";
	}
	private static boolean moveJobFromQueueToDoneFolder()
    {
		File jobDirSource=new File(jobPath);
		File jobDirDest=new File(doneJobPath);
    	try {
			Files.move(jobDirSource.toPath(), jobDirDest.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return true;
    }
	
	
	
	
	
	
	
	
	
	
	
    public static void zipIt(String zipFile) {
        byte[] buffer = new byte[1024];
        String source = new File(folderPathToMakeArchive).getName();
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        try {
            fos = new FileOutputStream(zipFile);
            zos = new ZipOutputStream(fos);

            System.out.println("Output to Zip : " + zipFile);
            FileInputStream in = null;

            for (String file: archiverFileList) {
                System.out.println("File Added : " + file);
                ZipEntry ze = new ZipEntry(source + File.separator + file);
                zos.putNextEntry(ze);
                try {
                    in = new FileInputStream(folderPathToMakeArchive + File.separator + file);
                    int len;
                    while ((len = in .read(buffer)) > 0) {
                        zos.write(buffer, 0, len);
                    }
                } finally {
                    in.close();
                }
            }

            zos.closeEntry();
            
            System.out.println("Folder successfully compressed");

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                zos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void generateFileList(File node) {
        // add file only
        if (node.isFile()) {
        	archiverFileList.add(generateZipEntry(node.toString()));
        }

        if (node.isDirectory()) {
            String[] subNote = node.list();
            for (String filename: subNote) {
                generateFileList(new File(node, filename));
            }
        }
    }

    private static String generateZipEntry(String file) {
        return file.substring(folderPathToMakeArchive.length() + 1, file.length());
    }

}