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
    int estado_mario = 0;
    int distancia_recorrida = -1;
    int distancia_final = -1;
    private Random R = null;
    static BufferedWriter fichero = null;  
    static BufferedReader Cluster_Inicial = null;
	String [] auxString = new String[25];
	String [] coin_bricks = new String[25];
	String [] arrayAuxiliar = new String[2];
	String [] arrayInicial = new String[2];
	public static String [][] array_cluster_inicial = new String[3][500];
	String miString = null;
	String escritura_final;
	StringBuffer aux_sb = new StringBuffer();
	StringBuffer sb = new StringBuffer();
	static int []cont_cluster = new int [3];
	
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
			Cluster_Inicial=new BufferedReader(new FileReader("Instancias_para_Seleccion.txt"));
			String linea = Cluster_Inicial.readLine();
			while(linea != null) {
				String [] parts_inicio = linea.split(", ");
				if(parts_inicio[19].equals("peligro") ) {
					array_cluster_inicial[0][cluster_peligro] = linea; 
					cluster_peligro++;
				}
				else if(parts_inicio[19].equals("no_danger") ) {
					array_cluster_inicial[1][cluster_no_danger] = linea; 
					cluster_no_danger++;
				}
				else {
					array_cluster_inicial[2][cluster_moneda] = linea;
					cluster_moneda++;
				}
				linea = Cluster_Inicial.readLine();
			}
			cont_cluster[0] = cluster_peligro;
			cont_cluster[1] = cluster_no_danger;
			cont_cluster[2] = cluster_moneda;
			
			fichero = new BufferedWriter(new FileWriter("Drogoz_IBL.arff", true));
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
			fichero.write("@ATTRIBUTE distancePassedCells NUMERIC\n");
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
    miString = null;
    escritura_final = null;
    aux_sb = null;
   	aux_sb = new StringBuffer();
   	sb = new StringBuffer();
       
   	// Devuelve un array de 19x19 donde Mario ocupa la posicion 9,9 con la union de los dos arrays
   	// anteriores, es decir, devuelve en un mismo array la informacion de los elementos de la
   	// escena y los enemigos.
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
  		miString = sb.append(String.valueOf(marioState[1]+", ")).toString(); 
  		estado_mario = marioState[1];
       miString = sb.append(String.valueOf(coins+", ")).toString(); 

       // Mas informacion de evaluacion...
       // distancePassedCells, distancePassedPhys, flowersDevoured, killsByFire, killsByShell, killsByStomp, killsTotal, marioMode,
       // marioStatus, mushroomsDevoured, coinsGained, timeLeft, timeSpent, hiddenBlocksFound
       int[] infoEvaluacion;
       infoEvaluacion = environment.getEvaluationInfoAsInts();
       distancia_recorrida = infoEvaluacion[0];
      miString = sb.append(String.valueOf(infoEvaluacion[10]+", ")).toString();
	  miString = sb.append(String.valueOf(infoEvaluacion[0]+", ")).toString(); 
       
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
   		
        int reward = environment.getIntermediateReward();
        escritura_final = sb.append(String.valueOf(reward+", ")).toString();
     
        getAction();

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
			e.printStackTrace();
		}
    }
		else {
			auxString[tick] = miString;
			coin_bricks[tick] = aux_sb.append(String.valueOf(infoEvaluacion[10])).toString();
		}
    tick++;
} 
    
    public boolean[] getAction() {
    if(tick > 25) { 
       //Inicio de deteccion de cluster ----- Primera parte de la Practica
	//String linea = Cluster_Inicial.readLine();
	String [] parts_escritura_final = escritura_final.split(", ");
	
	String primer_string = null;
	String segundo_string = null;
	String tercer_string = null;
	
	//Estos valores sirven para encontrar el menor valor de la funcion euclidea
	double primero = 100000; 
	double segundo = 100000;
	double tercero = 100000;
	
	int cluster_peligro = 0;
	int cluster_no_danger = 0;
	int cluster_moneda = 0;
	
	for(int i = 0; i < 3; i++ ) {
		for(int j = 0 ; j < cont_cluster[i]-1; j++) {
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
	if(cluster_final[19].equals("peligro") ) {
		cluster_peligro++;
	}
	else if(cluster_final[19].equals("no_danger") ) {
		cluster_no_danger++;
	}
	else {
		cluster_moneda++;
	}
	cluster_final = segundo_string.split(", ");
	if(cluster_final[19].equals("peligro") ) {
		cluster_peligro++;
	}
	else if(cluster_final[19].equals("no_danger") ) {
		cluster_no_danger++;
	}
	else {
		cluster_moneda++;
	}
	cluster_final = tercer_string.split(", ");
	if(cluster_final[19].equals("peligro") ) {
		cluster_peligro++;
	}
	else if(cluster_final[19].equals("no_danger") ) {
		cluster_no_danger++;
	}
	else {
		cluster_moneda++;
	}
	//Fin de deteccion de cluster ----- Primera parte de la Practica
	if(cluster_peligro < cluster_no_danger && cluster_moneda < cluster_no_danger) {
		action = seleccionar_intancias_accion(1, parts_escritura_final, action, estado_mario, distancia_recorrida); 
	}
	else if(cluster_peligro < cluster_moneda && cluster_moneda < cluster_moneda) {
		action = seleccionar_intancias_accion(2, parts_escritura_final, action, estado_mario, distancia_recorrida); 
	}
	else {
		action = seleccionar_intancias_accion(0, parts_escritura_final, action, estado_mario, distancia_recorrida); 
	}
   	}
    return action;
 }
    
    public static double euclidean(String[] x, String[] y){ 
        double sum = 0; 
        int peso = 1;
        for (int i=0; i<= 17; i++){
        	// i <= 8 valores del mapa, i == 10 coins , i == 15 coins_24; i == 16 timeleft
        	if(i <= 8 || i == 10 || i == 15 || i == 16) {
        		peso = 1;
        	}
        	// i == 9 marioMode, i == 11  coinsGained, i == 17 reward
        	else if(i == 9 || i == 11 || i == 13 || i == 17) {
        		peso = 2;
        	}
        	// i == 14 enemys
        	else if(i == 14) {
        		peso = 3;
        	}
        	// i == 12 foso
        	else if(i == 12) {
        		peso = 4;
        	}
            sum += peso*(Math.pow(Integer.parseInt(x[i]) - Integer.parseInt(y[i]), 2)); 
        }      
        return Math.sqrt(sum); 
    } 
    
    public boolean[] seleccionar_intancias_accion(int i, String[] parts_escritura_final, boolean [] action, int modo_mario, int distancia_recorrida) {
    	String primer_string = null;
		String segundo_string = null;
		String tercer_string = null;
		
		double primero = 100000; 
		double segundo = 100000;
		double tercero = 100000;
		
			for(int j = 0 ; j < cont_cluster[i]-1; j++) {
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
			String [] instancias_primero = primer_string.split(", ");
			String [] instancias_segundo = segundo_string.split(", ");
			String [] instancias_tercero = tercer_string.split(", ");
			if(Integer.parseInt(instancias_primero[15]) > Integer.parseInt(instancias_segundo[15]) && 
					Integer.parseInt(instancias_primero[15]) > Integer.parseInt(instancias_tercero[15]) ) {
				action = seleccion_action(instancias_primero, action, modo_mario, distancia_recorrida);
			}
			else if(Integer.parseInt(instancias_segundo[15]) > Integer.parseInt(instancias_primero[15]) && 
					Integer.parseInt(instancias_segundo[15]) > Integer.parseInt(instancias_tercero[15]) ) {
				action = seleccion_action(instancias_segundo, action, modo_mario, distancia_recorrida);
			}
			else if(Integer.parseInt(instancias_segundo[15]) < Integer.parseInt(instancias_tercero[15]) && 
					Integer.parseInt(instancias_primero[15]) < Integer.parseInt(instancias_tercero[15]) ) {
				action = seleccion_action(instancias_tercero, action, modo_mario, distancia_recorrida);
			}
			else if(Integer.parseInt(instancias_primero[17]) > Integer.parseInt(instancias_segundo[17]) && 
					Integer.parseInt(instancias_primero[17]) > Integer.parseInt(instancias_tercero[17]) ) {
				action = seleccion_action(instancias_primero, action, modo_mario, distancia_recorrida);
			}
			else if(Integer.parseInt(instancias_segundo[17]) > Integer.parseInt(instancias_primero[17]) && 
					Integer.parseInt(instancias_segundo[17]) > Integer.parseInt(instancias_tercero[17]) ) {
				action = seleccion_action(instancias_segundo, action, modo_mario, distancia_recorrida);
			}
			else if(Integer.parseInt(instancias_segundo[17]) < Integer.parseInt(instancias_tercero[17]) && 
					Integer.parseInt(instancias_primero[17]) < Integer.parseInt(instancias_tercero[17]) ) {
				action = seleccion_action(instancias_tercero, action, modo_mario, distancia_recorrida);
			}
			else {
				Random r = new Random();
				int valorDado = r.nextInt(3);
				if(valorDado == 0) {
					action = seleccion_action(instancias_primero, action, modo_mario, distancia_recorrida);
				}
				else if(valorDado == 1) {
					action = seleccion_action(instancias_segundo, action, modo_mario, distancia_recorrida);
				}
				else if(valorDado == 2) {
					action = seleccion_action(instancias_tercero, action, modo_mario, distancia_recorrida);
				}
			}
			return action;
    }
    
    public boolean [] seleccion_action(String []instancia, boolean [] action, int modo_mario, int distancia_recorrida) {
    	if(distancia_final == distancia_recorrida) {
    		Random r = new Random();
			int valorDado = r.nextInt(2);
			//Avanza
			if(valorDado == 0) {
				action[0] = false; 
				action[1] = true;
				action[2] = false;
				if(modo_mario == 2) {
					action[4] = true;
				}
				else {
					action[4] = false;
				}
				action[3] = false;
				action[5] = false;
			}
			// Salta - Avanza
			else if(valorDado == 1) {
				action[0] = false; 
				action[1] = true;
				action[2] = false;
				if(modo_mario == 2) {
					action[4] = true;
				}
				else {
					action[4] = false;
				}
				action[3] = true;
				action[5] = false;
			}
    	}	
    	else if(instancia[18].equals("PARADO")) {
			action[0] = false; 
			action[1] = false;
			action[2] = false;
			if(modo_mario == 2) {
				action[2] = true;
				action[4] = true;
			}
			else {
				action[2] = false;
				action[4] = false;
			}
			action[3] = false;
			action[5] = false;
		}
		else if(instancia[18].equals("SALTA")) {
			action[0] = false; 
			action[1] = false;
			if(modo_mario == 2) {
				action[2] = true;
				action[4] = true;
			}
			else {
				action[2] = false;
				action[4] = false;
			}
			action[3] = true;
			action[5] = false;
		}
		else if(instancia[18].equals("AVANZA")) {
			action[0] = false; 
			action[1] = true;
			action[2] = false;
			if(modo_mario == 2) {
				action[4] = true;
			}
			else {
				action[4] = false;
			}
			action[3] = false;
			action[5] = false;
		}
		else if(instancia[18].equals("RETROCEDE")) {
			action[0] = true; 
			action[1] = false;
			action[2] = false;
			if(modo_mario == 2) {
				action[4] = true;
			}
			else {
				action[4] = false;
			}
			action[3] = false;
			action[5] = false;
		}
		else if(instancia[18].equals("JUMP-RIGHT")) {
			action[0] = false; 
			action[1] = true;
			action[2] = false;
			if(modo_mario == 2) {
				action[4] = true;
			}
			else {
				action[4] = false;
			}
			action[3] = true;
			action[5] = false;
		}
		else if(instancia[18].equals("JUMP-LEFT")) {
			action[0] = true; 
			action[1] = false;
			action[2] = false;
			if(modo_mario == 2) {
				action[4] = true;
			}
			else {
				action[4] = false;
			}
			action[3] = true;
			action[5] = false;
		}
    	distancia_final = distancia_recorrida;
    	return action;
    } 
}