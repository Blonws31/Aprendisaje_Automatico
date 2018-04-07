/*
 * Copyright (c) 2009-2010, Sergey Karakovskiy and Julian Togelius
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Mario AI nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package ch.idsia.agents.controllers;

import ch.idsia.agents.Agent;
//import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;
import java.io.*;
import java.util.Random;

public class Drogoz_IBL extends BasicMarioAIAgent implements Agent {
	
    int tick;
    private Random R = null;
    static BufferedWriter fichero = null;  
    static BufferedReader Cluster_Inicial = null;
	String [] auxString = new String[25];
	String [] coin_bricks = new String[25];
	String [] arrayAuxiliar = new String[2];
	String [] arrayInicial = new String[2];
	static String [][] array_cluster_inicial = new String[3][300];
	String miString = null;
	String escritura_final;
	StringBuffer aux_sb = new StringBuffer();
	StringBuffer sb = new StringBuffer();
	
    public Drogoz_IBL() {
        super("BaselineAgent");
		escribir();
        reset();
        tick = 0;
    }
    
    public void reset() {
        // Dummy reset, of course, but meet formalities!
        R = new Random();    
    }
    
    public static void escribir()
    {
    	int cluster_peligro = 0;
    	int cluster_no_danger = 0;
    	int cluster_moneda = 0;
    	try {
			fichero = new BufferedWriter(new FileWriter("Drogoz_IBL.arff", true));
			Cluster_Inicial=new BufferedReader(new FileReader("P2HumanAgent.arff"));
			String linea = Cluster_Inicial.readLine();
			while(linea != null) {
				String [] parts_inicio = linea.split(", ");
				
				if(parts_inicio[20].equals("peligro") ) {
					array_cluster_inicial[0][cluster_peligro] = linea; 
					cluster_peligro++;
				}
				else if(parts_inicio[20].equals("no_danger") ) {
					array_cluster_inicial[1][cluster_no_danger] = linea; 
					cluster_no_danger++;
				}
				else {
					array_cluster_inicial[0][cluster_moneda] = linea; 
					cluster_moneda++;
				}
				linea = Cluster_Inicial.readLine();
			}
			String sFichero = "Drogoz_IBL.arff";
			File fichero = new File (sFichero);
			if(fichero.length() == 0) {
				Cabecera_ARFF();
			}
			//fichero.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    
    }
    
    public static void Cabecera_ARFF() {
    	try {
			fichero.write("@RELATION Drogoz_IBL\n\n");
			fichero.flush();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	
		try {
			fichero.write("@ATTRIBUTE celda[8][10] NUMERIC\n");
			fichero.write("@ATTRIBUTE celda[9][10] NUMERIC\n");
			fichero.write("@ATTRIBUTE celda[9][11] NUMERIC\n");
			fichero.write("@ATTRIBUTE celda[10][10] NUMERIC\n");
			fichero.write("@ATTRIBUTE celda[11][10] NUMERIC\n");
			fichero.write("@ATTRIBUTE celda[10][11] NUMERIC\n");
			fichero.write("@ATTRIBUTE celda[11][11] NUMERIC\n");
			fichero.write("@ATTRIBUTE celda[10][12] NUMERIC\n");
			fichero.write("@ATTRIBUTE celda[11][12] NUMERIC\n");
			fichero.write("@ATTRIBUTE marioMode NUMERIC\n");
			fichero.write("@ATTRIBUTE coins NUMERIC\n");
			fichero.write("@ATTRIBUTE coinsGained NUMERIC\n");
			fichero.write("@ATTRIBUTE foso {1, 0}\n");
			fichero.write("@ATTRIBUTE obstaculo {1, 0}\n");
			fichero.write("@ATTRIBUTE enemys {1, 0}\n");
			fichero.write("@ATTRIBUTE coins_24 NUMERIC\n");
			fichero.write("@ATTRIBUTE timeLeft NUMERIC\n");
			fichero.write("@ATTRIBUTE reward NUMERIC\n");
			fichero.write("@ATTRIBUTE ACTION {PARADO, SALTA, AVANZA, JUMP-ADVANCE}\n");
			fichero.write("@ATTRIBUTE cluster STRING\n");
			fichero.write("@data\n");
			fichero.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
    }
    
public void integrateObservation(Environment environment) {
	int foso = 0;
	int obstaculo = 0;
	int enemys = 0;
   	aux_sb = new StringBuffer();
   	sb = new StringBuffer();
       
   	// Devuelve un array de 19x19 donde Mario ocupa la posicion 9,9 con la union de los dos arrays
   	// anteriores, es decir, devuelve en un mismo array la informacion de los elementos de la
   	// escena y los enemigos.
   	//System.out.println("\nMERGE:");
   	byte [][] env;
   	env = environment.getMergedObservationZZ(1, 1);
   	int coins = 0;
   	for (int mx = 0; mx < env.length; mx++) {
   		for (int my = 0; my < env[mx].length; my++) {
   			if(env[mx][my] == 2) {
   				coins++;
   			}
   			if(env[mx][my] == 3 || env[mx][my] == 25) {
   				env[mx][my] = 0;
   			}
   		}
   	}
   	miString = sb.append(String.valueOf(env[8][10])+", "+String.valueOf(env[9][10])+", "+String.valueOf(env[9][11])+", "
   			+String.valueOf(env[10][10])+", "+String.valueOf(env[11][10])+", "
   			+String.valueOf(env[10][11])+", "+String.valueOf(env[11][11])+", "
   			+String.valueOf(env[10][12])+", "+String.valueOf(env[11][12])+", ").toString();
   	
    // Foso
    if(((env[6][9] == 00 || env[7][9] == 00 || env[8][9] == 00) && env[10][10] == 00 && env[10][11] == 00 && env[10][12] == 00 && env[11][10] == 00 
    		&& env[11][11] == 00 && env[11][12] == 00 )) {
    	foso = 1;
    }
    // Obstaculo -24 -60 -62 -85 
    if( env[8][10] == -85 || env[8][10] == -24 || env[8][10] == -62 || env[8][10] == -60 ||
 		   env[9][10] == -85 || env[9][10] == -24 || env[9][10] == -62 || env[9][10] == -60 ||
 		   env[9][11] == -85 || env[9][11] == -24 || env[9][11] == -62 || env[9][11] == -60) {
    	obstaculo = 1;
    }
    // Enemy 80
    if(env[8][10] == 80 || env[9][10] == 80 || env[9][11] == 80 ||  env[10][10] == 80 || env[10][11] == 80) {
    	enemys = 1;
    }
       // Estado de mario
       // marioStatus, marioMode, isMarioOnGround (1 o 0), isMarioAbleToJump() (1 o 0), isMarioAbleToShoot (1 o 0), 
       // isMarioCarrying (1 o 0), killsTotal, killsByFire,  killsByStomp, killsByShell, timeLeft
       int[] marioState;
       marioState = environment.getMarioState();
       for (int mx = 0; mx < marioState.length; mx++) {
       	if(mx == 1) {
       		miString = sb.append(String.valueOf(marioState[1]+", ")).toString(); 
       	}
       }
		
       miString = sb.append(String.valueOf(coins+", ")).toString(); 

       // Mas informacion de evaluacion...
       // distancePassedCells, distancePassedPhys, flowersDevoured, killsByFire, killsByShell, killsByStomp, killsTotal, marioMode,
       // marioStatus, mushroomsDevoured, coinsGained, timeLeft, timeSpent, hiddenBlocksFound
       int[] infoEvaluacion;
       infoEvaluacion = environment.getEvaluationInfoAsInts();
       for (int mx = 0; mx < infoEvaluacion.length; mx++){
       	if(mx == 10) {
           	miString = sb.append(String.valueOf(infoEvaluacion[10]+", ")).toString();
       	}
       }
       
      miString = sb.append(String.valueOf(foso+", "+obstaculo+", "+enemys+", ")).toString();
      if(tick >= 25) { 
    	escritura_final = auxString[tick%25];
    	auxString[tick%25] = miString;
    	
    	if((tick%25)+24 < 25) {
        	arrayAuxiliar = coin_bricks[((tick%25)+24)].split(", ");
        	arrayInicial = coin_bricks[(tick%25)].split(", ");
        }
        else {
        	arrayAuxiliar = coin_bricks[((tick%25)+24)%25].split(", ");
        	arrayInicial = coin_bricks[(tick%25)].split(", ");
        }
    	escritura_final = sb.append((Integer.parseInt(arrayAuxiliar[0]) -Integer.parseInt(arrayInicial[0]))+", ").toString();
    	escritura_final = sb.append(String.valueOf(infoEvaluacion[11]+", ")).toString();
        coin_bricks[tick%25] = aux_sb.append(String.valueOf(infoEvaluacion[10])).toString();
   		
        getAction();
        
        int reward = environment.getIntermediateReward();
        miString = sb.append(String.valueOf(reward+", ")).toString();

	    //action PARADO
        if(action[0] == false && action[1] == false && (action[2] == false || action[2] == true) && action[3] == false && (action[4] == false || 
   				action[4] == true ) && (action[5] == false || action[5] == true)) {
        	escritura_final = sb.append("PARADO").toString();   
   		}
	   	//action SALTA
        else if(action[0] == false && action[1] == false && (action[2] == false || action[2] == true)  && action[3] == true && (action[4] == false || 
	   			action[4] == true) && (action[5] == false || action[5] == true)) {
        	escritura_final = sb.append("SALTA, ").toString();   
	   	}
	   	//action AVANZA DERECHA
        else if((action[1] == true) && (action[2] == false || action[2] == true)  && action[3] == false && (action[4] == false || 
	   			action[4] == true) && (action[5] == false || action[5] == true)) {
        	escritura_final = sb.append("AVANZA, ").toString();   
        }
        //action RETROCEDE IZQUIERDA
        else if((action[0] == true) && (action[2] == false || action[2] == true)  && action[3] == false && (action[4] == false || 
    	   		action[4] == true) && (action[5] == false || action[5] == true)) {
            	escritura_final = sb.append("RETROCEDE, ").toString();   
    	}
	   	//action SALTA + DERECHA
        else if((action[1] == true) && (action[2] == false || action[2] == true)  && action[3] == true && (action[4] == false || 
	   			action[4] == true) && (action[5] == false || action[5] == true)) {
        	escritura_final = sb.append("JUMP-RIGHT, ").toString();  
	   	}
      //action SALTA + IZQUIERDA
        else if((action[0] == true) && (action[2] == false || action[2] == true)  && action[3] == true && (action[4] == false || 
	   			action[4] == true) && (action[5] == false || action[5] == true)) {
        	escritura_final = sb.append("JUMP-LEFT, ").toString();  
	   	}
        
        // Cluster no_danger, Peligro, Moneda
        
        // Peligro
        if(     env[8][10] == -85 || env[8][10] == -24 || env[8][10] == -62 || env[8][10] == 80 || env[8][10] == -60 ||
     		   env[9][10] == -85 || env[9][10] == -24 || env[9][10] == -62 || env[9][10] == 80 || env[9][10] == -60 ||
     		   env[9][11] == -85 || env[9][11] == -24 || env[9][11] == -62 || env[9][11] == 80 || env[9][11] == -60 ||
     		   env[10][10] == 80 || env[10][11] == 80 || ((env[6][9] == 00 || env[7][9] == 00 || env[8][9] == 00) &&
         	   env[10][10] == 00 && env[10][11] == 00 && env[10][12] == 00 && env[11][10] == 00 && env[11][11] == 00 && env[11][12] == 00 ))	    
     		    {
        	escritura_final = sb.append("peligro\n").toString();  
     	}
        // Moneda
        else if(env[6][9] == 2 || env[7][9] == 2 || env[8][9] == 2 || env[8][10] == 2 || env[9][10] == 2) {
        	escritura_final = sb.append("moneda\n").toString();  
        }
        // no_danger 
        else {
        	escritura_final = sb.append("no_danger\n").toString();  
        }
    	try {
			fichero.write(escritura_final);
			fichero.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
		else {
			auxString[tick] = miString;
			coin_bricks[tick] = aux_sb.append(String.valueOf(infoEvaluacion[10])).toString();
		}
    tick++;
    miString = null;
    escritura_final = null;
    aux_sb = null;
} 
    
    public boolean[] getAction() {
        // La accion es un array de booleanos de dimension 6
        // action[Mario.KEY_LEFT] Mueve a Mario a la izquierda
        // action[Mario.KEY_RIGHT] Mueve a Mario a la derecha
        // action[Mario.KEY_DOWN] Mario se agacha si esta en estado grande
        // action[Mario.KEY_JUMP] Mario salta
        // action[Mario.KEY_SPEED] Incrementa la velocidad de Mario y dispara si esta en modo fuego
        // action[Mario.KEY_UP] Arriba

    	for (int i = 0; i < Environment.numberOfKeys; ++i) {
            boolean toggleParticularAction = R.nextBoolean();
            toggleParticularAction = (i == 0 && toggleParticularAction && R.nextBoolean()) ? R.nextBoolean() : toggleParticularAction;
            toggleParticularAction = (i == 1 || i > 3 && !toggleParticularAction) ? R.nextBoolean() : toggleParticularAction;
            toggleParticularAction = (i > 3 && !toggleParticularAction) ? R.nextBoolean() : toggleParticularAction;
            action[i] = toggleParticularAction;
        }
    if(tick > 25) { 
       //Inicio de deteccion de cluster ----- Primera parte de la Practica
	//String linea = Cluster_Inicial.readLine();
	String [] parts_escritura_final = escritura_final.split(", ");
	
	String primer_string = null;
	String segundo_string = null;
	String tercer_string = null;
	
	double primero = 100000; 
	double segundo = 100000;
	double tercero = 100000;
	
	int cluster_peligro = 0;
	int cluster_no_danger = 0;
	int cluster_moneda = 0;
	for(int i = 0; i < 3; i++ ) {
		for(int j = 0 ; j < array_cluster_inicial[i].length; j++) {
			String [] parts_linea = array_cluster_inicial[i][j].split(", ");
			double euclidean = euclidean(parts_linea,parts_escritura_final);
			if(primero >= euclidean) {
				primero = euclidean;
				primer_string = array_cluster_inicial[i][j];
			}
			else if(segundo >= euclidean) {
				segundo = euclidean;
				segundo_string = array_cluster_inicial[i][j];
			}
			else if(tercero >= euclidean) {
				tercero = euclidean;
				tercer_string = array_cluster_inicial[i][j];
			}
		}
	}

	String [] cluster_final = primer_string.split(", ");
	if(cluster_final[20].equals("peligro") ) {
		cluster_peligro++;
	}
	else if(cluster_final[20].equals("no_danger") ) {
		cluster_no_danger++;
	}
	else {
		cluster_moneda++;
	}
	cluster_final = segundo_string.split(", ");
	if(cluster_final[20].equals("cluster0") ) {
		cluster_peligro++;
	}
	else if(cluster_final[20].equals("cluster1") ) {
		cluster_no_danger++;
	}
	else {
		cluster_moneda++;
	}
	cluster_final = tercer_string.split(", ");
	if(cluster_final[20].equals("cluster0") ) {
		cluster_peligro++;
	}
	else if(cluster_final[20].equals("cluster1") ) {
		cluster_no_danger++;
	}
	else {
		cluster_moneda++;
	}
	//Fin de deteccion de cluster ----- Primera parte de la Practica
	
	if(cluster_peligro < cluster_no_danger && cluster_moneda < cluster_no_danger) {
		System.out.println("Cluster No Danger\n");
	}
	if(cluster_peligro < cluster_moneda && cluster_moneda < cluster_moneda) {
		System.out.println("Cluster Moneda\n");
	}
	else {
		System.out.println("Cluster Peligro\n");
	}
       
	//action PARADO
    if(action[0] == false && action[1] == false && (action[2] == false || action[2] == true) && action[3] == false && (action[4] == false || 
				action[4] == true ) && (action[5] == false || action[5] == true)) {
    	escritura_final = sb.append("PARADO\n").toString();   
		}
   	//action Salta
    else if(action[0] == false && action[1] == false && (action[2] == false || action[2] == true)  && action[3] == true && (action[4] == false || 
   			action[4] == true) && (action[5] == false || action[5] == true)) {
    	escritura_final = sb.append("SALTA\n").toString();   
   	}
   	//action Avanza
    else if((action[0] == true || action[1] == true) && (action[2] == false || action[2] == true)  && action[3] == false && (action[4] == false || 
   			action[4] == true) && (action[5] == false || action[5] == true)) {
    	escritura_final = sb.append("AVANZA\n").toString();   
   	}
   	//action Salta + Avanza
    else if((action[0] == true || action[1] == true) && (action[2] == false || action[2] == true)  && action[3] == true && (action[4] == false || 
   			action[4] == true) && (action[5] == false || action[5] == true)) {
    	escritura_final = sb.append("JUMP-ADVANCE\n").toString();  
   	}
   	}
       return action;
    }
    
    public static double euclidean(String[] x, String[] y){ 
        double sum = 0; 
        for (int i=0; i< 17; i++){ 
            sum += Math.pow(Integer.parseInt(x[i]) - Integer.parseInt(y[i]), 2); 
        }      
        return Math.sqrt(sum); 
    } 
    
    public int prediccion_coins_24(int[] marioState, int[] infoEvaluacion, int coins, int bricks,  String action) {
    	int prediccion = 0;
    	for(int i = 0; i < 5; i++) {
    		if(bricks == i) {
    			prediccion = 0;
    		}
    	}
    	if(bricks == 5) {
    		if(coins == 2) {
    			if(marioState[2] == 1) {
    				prediccion = 4;
    			}
    			else {
    				prediccion = 0;
    			}
    		}
    		else if(coins == 4) {
    			prediccion = 3;
    		}
    		else if(coins == 6) {
    			if(action.equals("AVANZA")) {
    				prediccion = 0;
    			}
    			else if(action.equals("JUMP-ADVANCE")) {
    				prediccion = 2;
    			}
    			else {
    				prediccion = 4;
    			}
    		}
    		else if(coins == 7) {
    			prediccion = 3;
    		}
    		else {
    			prediccion = 0;
    		}
    		
    	}
    	else if(bricks == 6) {
    		if(coins == 0) {
    			if(action.equals("AVANZA")) {
    				if(marioState[2] == 1) {
    					prediccion = 0;
    				}
    				else {
    					prediccion = 4;
    				}
    			}
    			else if(action.equals("JUMP-ADVANCE")) {
    				prediccion = 4;
    			}
    			else {
    				prediccion = 0;
    			}
    		}
    		else if(coins == 1) {
    			prediccion = 3;
    		}
    		else if(coins == 2 || coins == 3) {
    			prediccion = 4;
    		}
    		else if(coins == 4) {
    			if(marioState[3] == 1) {
    				prediccion = 0;
    			}
    			else {
    				if(action.equals("JUMP-ADVANCE")) {
    					prediccion = 2;
    				}
    				else {
    					prediccion = 3;
    				}
    			}
    		}
    		else if(coins == 7) {
    			if(marioState[1] == 0 || marioState[1] == 1) {
    				prediccion = 0;
    			}
    			else {
    				if(marioState[2] == 1) {
    					prediccion = 3;
    				}
    				else {
    					prediccion = 0;
    				}
    			}
    		}
    		else if(coins == 8) {
    			prediccion = 5;
    		}
    		else if(coins == 10) {
    			prediccion = 3;
    		}
    		else {
    			prediccion = 0;
    		}
    	}
    	else if(bricks == 7) {
    		if(action.equals("JUMP-ADVANCE")) {
    			if(coins == 2 || coins == 4 || coins == 6) {
    				prediccion = 4;
    			}
    			else {
    				prediccion = 0;
    			}
    		}
    		else {
    			prediccion = 0;
    		}
    	}
    	else if(bricks == 8) {
    		if(coins == 0) {
    			prediccion = 4;
    		}
    		else if(coins == 1 || coins == 2) {
    			prediccion = 1;
    		}
    		else if(coins == 5) {
    			if(marioState[2] == 1) {
    				prediccion = 4;
    			}
    			else {
    				prediccion = 0;
    			}
    		}
    		else if(coins == 11) {
    			if(action.equals("PARADO") || action.equals("SALTA")) {
    				prediccion = 2;
    			}
    			else {
    				prediccion = 0;
    			}
    		}
    		else {
    			prediccion = 0;
    		}
    	}
    	else if(bricks == 9) {
    		if(coins == 5) {
    			if(action.equals("AVANZA")) {
    				prediccion = 6;
    			}
    			else {
    				prediccion = 0;
    			}
    		}
    		else if(coins == 6) {
    			if(action.equals("AVANZA")) {
    				prediccion = 0;
    			}
    			else {
    				prediccion = 5;
    			}
    		}
    		else if(coins == 9 || coins == 1) {
    			prediccion = 1;
    		}
    		else {
    			prediccion = 0;
    		}
    	}
    	else if(bricks == 10) {
    		if(coins == 3) {
    			prediccion = 6;
    		}
    		else if(coins == 8 || coins == 12) {
    			prediccion = 1;
    		}
    		else if(coins == 14) {
    			prediccion = 2;
    		}
    		else {
    			prediccion = 0;
    		}
    	}
    	else if(bricks == 11) {
    		if(coins == 7) {
    			prediccion = 2;
    		}
    		else if(coins == 8) {
    			if(marioState[3] == 1) {
    				prediccion = 0;
    			}
    			else {
    				prediccion = 1;
    			}
    		}
    		else if(coins == 10) {
    			prediccion = 3;
    		}
    		else if(coins == 11) {
    			if(marioState[1] == 0) {
    				prediccion = 1;
    			}
    			else {
    				prediccion = 2;
    			}
    		}
    		else {
    			prediccion = 0;
    		}
    	}
    	else if(bricks == 12) {
    		if(coins == 1) {
    			prediccion = 1;
    		}
    		else if(coins == 3 || coins == 5) {
    			prediccion = 6;
    		}
    		else if(coins == 6) {
    			if(marioState[2] == 1) {
    				prediccion = 6;
    			}
    			else {
    				prediccion = 4;
    			}
    		}
    		else if(coins == 8) {
    			if(marioState[1] == 2) {
    				if(marioState[3] == 1) {
    					prediccion = 4;
    				}
    				else {
    					prediccion = 6;
    				}
    			}
    			else {
    				prediccion = 3;
    			}
    		}
    		else if(coins == 9) {
    			prediccion = 3;
    		}
    		else if(coins == 10) {
    			if(marioState[3] == 1) {
    				prediccion = 4;
    			}
    			else {
    				prediccion = 2;
    			}
    		}
    		else if(coins == 11) {
    			if(marioState[1] == 0) {
    				if(action.equals("JUMP-ADVANCE")) {
    					prediccion = 2;
    				}
    				else {
    					prediccion = 3;
    				}
    			}
    		}
    		else {
    			prediccion = 0;
    		}
    	}
    	else if(bricks == 13) {
    		if(marioState[1] == 0) {
    			if(coins == 11) {
    				prediccion = 2;
    			}
    			else if(coins == 13) {
    				prediccion = 0;
    			}
    			else {
    				prediccion = 1;
    			}
    		}
    		else if(marioState[1] == 1) {
    			prediccion = 7;
    		}
    		else if(marioState[1] == 2) {
    			if(coins == 2 || coins == 4) {
    				prediccion = 2;
    			}
    			else if(coins == 5) {
    				prediccion = 5;
    			}
    			else if(coins == 6 || coins == 10) {
    				prediccion = 6;
    			}
    			else if(coins == 8) {
    				if(marioState[3] == 1) {
    					prediccion = 1;
    				}
    				else {
    					prediccion = 4;
    				}
    			}
    			else if(coins == 13) {
    				prediccion = 3;
    			}
    			else if(coins == 18) {
    				prediccion = 4;
    			}
    			else {
    				prediccion = 0;
    			}
    		}
    	}
    	else if(bricks == 14) {
    		if(marioState[1] == 0) {
    			prediccion = 7;
    		}
    		else if(marioState[1] == 1) {
    			prediccion = 6;
    		}
    		else {
    			if(coins == 8) {
    				prediccion = 1;
    			}
    			else if(coins == 10 || coins == 12) {
    				prediccion = 4;
    			}
    			else if(coins == 13) {
    				prediccion = 3;
    			}
    			else if(coins == 14) {
    				if(marioState[2] == 1) {
    					prediccion = 1;
    				}
    				else {
    					prediccion = 2;
    				}
    			}
    			else if(coins == 15) {
    				prediccion = 1;
    			}
    			else if(coins == 16) {
    				if(marioState[2] == 1) {
    					prediccion = 2;
    				}
    				else {
    					prediccion = 0;
    				}
    			}
    			else if(coins == 15) {
    				prediccion = 1;
    			}
    			else if(coins == 16) {
    				if(marioState[2] == 1) {
    					prediccion = 2;
    				}
    				else {
    					prediccion = 0;
    				}
    			}
    			else {
    				prediccion = 0;
    			}
    		}
    	}
    	else if(bricks == 15) {
    		if(coins == 6) {
    			prediccion = 1;
    		}
    		else if(coins == 10) {
    			if(action.equals("PARADO") || action.equals("SALTA")) {
    				prediccion = 5;
    			}
    			else {
    				prediccion = 0;
    			}
    		}
    		else if(coins == 11) {
    			if(marioState[2] == 1) {
    				prediccion = 2;
    			}
    			else {
    				prediccion = 6;
    			}
    		}
    		else if(coins == 13 || coins == 22) {
    			prediccion = 2;
    		}
    		else if(coins == 14 || coins == 18) {
    			prediccion = 5;
    		}
    		else if(coins == 20) {
    			prediccion = 4;
    		}
    		else {
    			prediccion = 0;
    		}
    	}
    	else if(bricks == 16) {
    		if(coins == 6 || coins == 15 || coins == 22) {
    			prediccion = 1;
    		}
    		else if(coins == 9 || coins == 10) {
    			prediccion = 4;
    		}
    		else if(coins == 11) {
    			prediccion = 6;
    		}
    		else {
    			prediccion = 0;
    		}
    	}
    	else if(bricks == 17) {
    		if(marioState[2] == 1) {
    			prediccion = 8;
    		}
    		else {
    			prediccion = 0;
    		}
    	}
    	else if(bricks == 18) {
    		if(coins == 7) {
    			prediccion = 5;
    		}
    		else if(coins == 11 || coins == 13) {
    			prediccion = 2;
    		}
    		else if(coins == 15 || coins == 16 || coins == 18) {
    			prediccion = 0;
    		}
    		else if(coins == 17) {
    			prediccion = 4;
    		}
    		else {
    			prediccion = 1;
    		}
    	}
    	else if(bricks == 19) {
    		if(action.equals("PARADO")) {
    			if(marioState[2] == 1) {
    				prediccion = 7;
    			}
    			else {
    				prediccion = 8;
    			}
    		}
    		else if(action.equals("SALTA")) {
    			prediccion = 8;
    		}
    		else if(action.equals("AVANZA")){
    			if(coins == 4) {
    				prediccion = 9;
    			}
    			else if(coins == 7) {
    				if(marioState[2] == 1) {
    					prediccion = 0;
    				}
    				else {
    					prediccion = 1;
    				}
    			}
    			else if(coins == 8) {
    				prediccion = 3;
    			}
    			else if(coins == 9) {
    				prediccion = 1;
    			}
    			else if(coins == 15) {
    				prediccion = 2;
    			}
    			else if(coins == 17) {
    				prediccion = 4;
    			}
    			else {
    				prediccion = 0;
    			}
    		}
    		else {
    			if(coins == 6) {
    				prediccion = 3;
    			}
    			else if(coins == 8) {
    				prediccion = 7;
    			}
    			else if(coins == 10 || coins == 13 || coins == 17 || coins == 18) {
    				prediccion = 0;
    			}
    			else if(coins == 16 || coins == 19) {
    				prediccion = 2;
    			}
    			else {
    				prediccion = 1;
    			}
    		}
    	}
    	else if(bricks == 20) {
    		if(marioState[1] == 0) {
    			prediccion = 0;
    		}
    		else if(marioState[1] == 1) {
    			prediccion = 1;
    		}
    		else {
    			if(marioState[2] == 1) {
    				prediccion = 7;
    			}
    			else {
    				prediccion = 2;
    			}
    		}
    	}
    	else if(bricks == 21) {
    		if(coins == 2) {
    			prediccion = 9;
    		}
    		else if(coins == 11 || coins == 16) {
    			prediccion = 0;
    		}
    		else if(coins == 12) {
    			prediccion = 6;
    		}
    		else if(coins == 15) {
    			prediccion = 4;
    		}
    		else if(coins == 20) {
    			prediccion = 10;
    		}
    		else {
    			prediccion = 2;
    		}
    	}
    	else if(bricks == 22) {
    		if(coins == 5) {
    			prediccion = 9;
    		}
    		else if(coins == 14) {
    			prediccion = 8;
    		}
    		else if(coins == 16) {
    			prediccion = 5;
    		}
    		else if(coins == 17 || coins == 23) {
    			prediccion = 0;
    		}
    		else if(coins == 20) {
    			prediccion = 10;
    		}
    		else {
    			prediccion = 4;
    		}
    	}
    	else if(bricks == 23) {
    		if(marioState[1] == 0 || marioState[1] == 1) {
    			prediccion = 0;
    		}
    		else {
    			if(coins == 13 || coins == 15) {
    				prediccion = 4;
    			}
    			else if(coins == 14 || coins == 16) {
    				prediccion = 2;
    			}
    			else if(coins == 18) {
    				prediccion = 5;
    			}
    			else if(coins == 20) {
    				prediccion = 10;
    			}
    			else {
    				prediccion = 6;
    			}
    		} 
    	}
    	else if(bricks == 24) {
    		if(marioState[1] == 2) {
    			prediccion = 5;
    		} 
    		else {
    			prediccion = 0;
    		}
    	}
    	else if(bricks == 25) {
    		if(action.equals("AVANZA")) {
    			prediccion = 1;
    		}
    		else if(action.equals("JUMP-ADVANCE")) {
    			prediccion = 9;
    		}
    		else {
    			prediccion = 4;
    		}
    	}
    	else if(bricks == 26) {
    		if(marioState[1] == 2) {
    			prediccion = 3;
    		}
    		else {
    			prediccion = 0;
    		}
    	}
    	else if(bricks == 27) {
    		if(marioState[3] == 1) {
    			prediccion = 8;
    		}
    		else {
    			prediccion = 4;
    		}
    	}
    	else if(bricks == 28) {
    		if(coins == 17 || coins == 19) {
    			prediccion = 5;
    		}
    		else if(coins == 21) {
    			prediccion = 4;
    		}
    		else if(coins == 22) {
    			if(action.equals("PARADO")) {
    				prediccion = 1;
    			}
    			else if(action.equals("SALTA")) {
    				prediccion = 2;
    			}
    			else {
    				prediccion = 0;
    			}
    		}
    		else if(coins == 23) {
    			prediccion = 1;
    		}
    		else if(coins == 24) {
    			prediccion = 0;
    		}
    		else if(coins == 25 || coins == 26) {
    			prediccion = 7;
    		}
    		else {
    			prediccion = 2;
    		}
    	}
    	else if(bricks == 29) {
    		if(marioState[1] == 0) {
    			prediccion = 1;
    		}
    		else if(marioState[1] == 1) {
    			prediccion = 4;
    		}
    		else {
    			prediccion = 6;
    		}
    	}
    	else if(bricks == 30) {
    		if(action.equals("SALTA")) {
				prediccion = 3;
			}
			else {
				prediccion = 1;
			}
    	}
    	else if(bricks == 31) {
    		if(marioState[1] == 0) {
    			prediccion = 2;
    		}
    		else if(marioState[1] == 1) {
    			if(action.equals("SALTA") || action.equals("PARADO")) {
    				prediccion = 1;
    			}
    			else if(action.equals("AVANZA")) {
    				prediccion = 2;
    			}
    			else {
    				prediccion = 0;
    			}
    		}
    		else {
    			prediccion = 6;
    		}
    	}
    	else if(bricks == 32) {
    		if(marioState[2] == 1) {
    			prediccion = 6;
    		}
    		else {
    			prediccion = 0;
    		}
    	}
    	else if(bricks == 33) {
    		if(coins == 18 || coins == 22) {
    			prediccion = 0;
    		}
    		else if(coins == 27) {
    			prediccion = 5;
    		}
    		else if(coins == 33) {
    			prediccion = 3;
    		}
    		else {
    			prediccion = 4;
    		}
    	}
    	else if(bricks == 34) {
    		if(marioState[3] == 1) {
    			prediccion = 6;
    		}
    		else {
    			prediccion = 4;
    		}
    	}
    	else if(bricks == 35) {
    		prediccion = 5;
    	}
    	else if(bricks == 36) {
    		prediccion = 5;
    	}
    	else if(bricks == 37) {
    		prediccion = 5;
    	}
    	return prediccion;
    }
}
