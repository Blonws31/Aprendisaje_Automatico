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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Drogoz_Refuerzo extends BasicMarioAIAgent implements Agent {
	
	static List<Tupla> mapa  = new ArrayList<Tupla>();
    //"PARADO", "SALTA", "AVANZA", "RETROCEDE", "JUMP-RIGHT", "JUMP-LEFT"
    static String[] acciones = {"0", "1", "2", "3", "4", "5"};
    //"foso", "enemigo", "obstaculo", "moneda", "no_danger"
    static String[] estados  = {"0", "1", "2", "3", "4"};
    
    static QLearning ql = new QLearning(0, 0.8, 0.2, estados, acciones, 5, 6);

    int tick;
    int aux_tick;
    int estado_mario = 0;
    int distancia_recorrida = -1;
    int distancia_final = -1;
    
	int foso;
	int obstaculo;
	int enemys;
	int moneda;
    int ciclosMaximos   = 5;
    int ciclos          = 0;

    int [] salto_array = new int [6] ;
    		
    boolean Salto;

	double[] estado = {0};
    double[] accion = {0};

    private Random R = null;
    static BufferedWriter fichero = null;  
    static BufferedReader Cluster_Inicial = null;
    static int datos_fichero = 1377;
	public static String [] array_tuplas_inicial = new String[datos_fichero];
	public static String [] array_tuplas_final = new String[datos_fichero];
    static int []cont_cluster = new int [5];

	String [] auxString = new String[25];
	String [] arrayInicial = new String[2];
	String miString = null;
	String escritura_final;
	
	static StringBuffer sb = new StringBuffer();

  
    public Drogoz_Refuerzo() {
        super("BaselineAgent");
		escribir();
		Tuplas();
        reset();
        tick = 0;   
   }
    
    public void reset() {
        // Dummy reset, of course, but meet formalities!
        R = new Random();    
    }
    
    public static void escribir()
    {
    	try {
			Cluster_Inicial=new BufferedReader(new FileReader("Instancias_para_Refuerzo.txt"));
			String linea = Cluster_Inicial.readLine();
			int i = 0;
			while(linea != null) {
				array_tuplas_inicial[i] = linea;
				i++;
				linea = Cluster_Inicial.readLine();
			}
			
			fichero = new BufferedWriter(new FileWriter("Drogoz_Refuerzo.arff", true));
			String sFichero = "Drogoz_Refuerzo.arff";
			File fichero = new File (sFichero);
			if(fichero.length() == 0) {
				//Cabecera_ARFF();
			}
			//fichero.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    
    }
    
    public void Tuplas() {

    	for(int i=0; i<493;i++) {
    		if(i != 492) {
    			String [] parts_inicio = array_tuplas_inicial[i].split(", ");
        		String [] parts_final = array_tuplas_inicial[(i+1)].split(", ");
        		array_tuplas_final[i] = sb.append(String.valueOf(parts_inicio[36]+", "+parts_inicio[17]+", "+parts_final[36]+", "+parts_inicio[35])).toString();
        		mapa.add(new Tupla(Double.parseDouble(parts_inicio[36]), Double.parseDouble(parts_inicio[17]), 
        				Double.parseDouble(parts_final[36]), Double.parseDouble(parts_inicio[35]))); 
    		}	
    	}
    	while (ciclos < ciclosMaximos)
        {
            for (int i = 0; i < mapa.size(); i++)
                ql.actualizarTablaQ(mapa.get(i));
            ciclos++;
        }
    	ql.mostrarTablaQ();
    }
    
    
    public static void Cabecera_ARFF() {
    	try {
			fichero.write("@RELATION Drogoz_Refuerzo\n\n");
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
			fichero.write("@ATTRIBUTE timeLeft NUMERIC\n");
			fichero.write("@ATTRIBUTE ACTION {PARADO, SALTA, AVANZA, RETROCEDE, JUMP-RIGHT, JUMP-LEFT}\n");
			fichero.write("@ATTRIBUTE ticks_24 STRING\n");
			fichero.write("@ATTRIBUTE reward NUMERIC\n");
			fichero.write("@ATTRIBUTE cluster STRING\n");
			fichero.write("@data\n");
			fichero.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
    }
    
    
    public void integrateObservation(Environment environment) {
	foso = 0;
	obstaculo = 0;
	enemys = 0;
	moneda = 0;
    Salto = false;
    miString = null;
    escritura_final = null;
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
    if(((env[6][9] == 0 || env[7][9] == 0 || env[8][9] == 0) && env[10][10] == 0 && env[10][11] == 0 && env[10][12] == 0 && env[11][10] == 0 
    		&& env[11][11] == 0 && env[11][12] == 0 && env[12][10] == 0 && env[12][11] == 0 && env[12][12] == 0 && env[13][10] == 0 
    	    		&& env[13][11] == 0 && env[13][12] == 0) ) {
    	foso = 1;
    	Salto = true;
    }
    // Obstaculo -24 -60 -62 -85 
    if( env[8][10] == -85 || env[8][10] == -24 || env[8][10] == -62 || env[8][10] == -60 ||
   		   env[9][10] == -85 || env[9][10] == -24 || env[9][10] == -62 || env[9][10] == -60 ||
   		   env[9][11] == -85 || env[9][11] == -24 || env[9][11] == -62 || env[9][11] == -60 ||
   		   (env[6][10] == -24  && env[9][10] == -24) || (env[7][10] == -24  && env[10][10] == -24) ||
   		   (env[8][10] == -24  && env[11][9] == -24) || (env[9][10] == -24  && env[12][9] == -24)) {
    	obstaculo = 1;
    	Salto = true;
    }
    //moneda
    if( env[8][10] == 2 || env[9][10] == 2 || env[9][11] == 2) {
    	moneda = 1;
    }
    // Enemy 80
    if(env[8][10] == 80 || env[9][8] == 80 || env[9][9] == 80 || env[9][10] == 80)  {
    	enemys = 1;
    	Salto = true;
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
    	
    	escritura_final = sb.append(String.valueOf(infoEvaluacion[11]+", ")).toString();
        aux_tick = tick;
        distancia_recorrida = infoEvaluacion[0];

        getAction();

	    //action PARADO
        if(action[0] == false && action[1] == false && (action[2] == false || action[2] == true) && action[3] == false && (action[4] == false || 
   				action[4] == true ) && (action[5] == false || action[5] == true)) {
        	escritura_final = sb.append("PARADO, ").toString();   
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
        
    	escritura_final = sb.append(String.valueOf(miString+", ")).toString();
        
        int reward = environment.getIntermediateReward();
        escritura_final = sb.append(String.valueOf(reward+", ")).toString();
        
        // Pozo
        if( foso == 1 )	{
        	escritura_final = sb.append("foso\n").toString();  
     	}
        //Enemigo
        else if( enemys == 1 ) {
         	escritura_final = sb.append("enemys\n").toString();  
      	}
        //Obstaculo
        else if( obstaculo == 1 ) {
         	escritura_final = sb.append("obstaculo\n").toString();  
      	}
        // Moneda
        else if( moneda == 1 ) {
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
	}
    tick++;
} 
    
    
    public boolean[] getAction() {
    	if(aux_tick == tick) {

	    	// 0 = "foso", 1 = "enemigo", 2 = "obstaculo", 3 = "moneda", 4 = "no_danger"};
	    	// Foso
	        if( foso == 1 )	{
	        	estado[0] = 0;
	        	 accion = ql.obtenerMejorAccion(estado);
	        	 System.out.println("Foso :"+tick);
	     	}
	        //Enemigo
	        else if( enemys == 1 ) {
	        	estado[0] = 1;
	        	 accion = ql.obtenerMejorAccion(estado);
	        	 System.out.println("Enemy :"+tick);
	      	}
	        //Obstaculo
	        else if( obstaculo == 1 ) {
	        	estado[0] = 2;
	        	 accion = ql.obtenerMejorAccion(estado);
	        	 System.out.println("Obstaculo :"+tick);
	      	}
	        // Moneda
	        else if( moneda == 1 ) {
	        	 estado[0] = 3;
	        	 accion = ql.obtenerMejorAccion(estado);
	        	 System.out.println("Moneda :"+tick);
	        }
	        // no_danger 
	        else {
	        	estado[0] = 4;
	        	 accion = ql.obtenerMejorAccion(estado);
	        	 System.out.println("No_danger :"+tick);
	        }

	 // La accion es un array de booleanos de dimension 6
        // action[Mario.KEY_LEFT] Mueve a Mario a la izquierda
        // action[Mario.KEY_RIGHT] Mueve a Mario a la derecha
        // action[Mario.KEY_DOWN] Mario se agacha si esta en estado grande
        // action[Mario.KEY_JUMP] Mario salta
        // action[Mario.KEY_SPEED] Incrementa la velocidad de Mario y dispara si esta en modo fuego
        // action[Mario.KEY_UP] Arriba
        
	    //action PARADO
	    if(accion[0] == 0) {
	    	action[0] = false;
	    	action[1] = false;
	        action[2] = false;
	        action[3] = false;
	        action[4] = false;
	        action[5] = false;
	    }
	    
	   	//action SALTA
	    if(accion[0] == 1) {
	    	action[0] = false;
	    	action[1] = false;
	        action[2] = false;
	        if(Salto == true) {
	        	if(salto_array[1] >= 0 && salto_array[1] <= 6) {
		     		   action[3] = true;
		     		   salto_array[1]++;
		     	   }
		     	   else{
		     		   action[3] = false;
		     		   salto_array[1] = 0;
		     	   }		   
	        }
	        action[4] = false;
	        action[5] = false;
	    }
        
	   	//action AVANZA DERECHA
	    if(accion[0] == 2) {
	    	action[0] = false;
	    	action[1] = true;
	        action[2] = false;
	        action[3] = false;
	        if(estado_mario == 2) {
	        	action[4] = true;
	        }
	        else {
	        	action[4] = false;
	        }
	        action[5] = false;
	    }
	    
        //action RETROCEDE IZQUIERDA
	    if(accion[0] == 3) {
	    	action[0] = true;
	    	action[1] = false;
	        action[2] = false;
	        action[3] = false;
	        if(estado_mario == 2) {
	        	action[4] = true;
	        }
	        else {
	        	action[4] = false;
	        }
	        action[5] = false;
	    }
        
	   	//action SALTA + DERECHA
	    if(accion[0] == 4) {
	    	action[0] = false;
	    	action[1] = true;
	        action[2] = false;
	        if(Salto == true ) {
	     	   if(salto_array[4] >= 0 && salto_array[4] <= 6) {
	     		   action[3] = true;
	     		   salto_array[4]++;
	     	   }
	     	   else{
	     		   action[3] = false;
	     		   salto_array[4] = 0;
	     	   }	   
	        }
	        if(estado_mario == 2) {
	        	action[4] = true;
	        }
	        else {
	        	action[4] = false;
	        }
	        action[5] = false;
	    }
        
	    //action SALTA + IZQUIERDA
	    if(accion[0] == 5) {
	    	action[0] = true;
	    	action[1] = false;
	        action[2] = false;
	        if(Salto == true) {
	        	if(salto_array[5] >= 0 && salto_array[5] <= 6) {
		     		   action[3] = true;
		     		   salto_array[5]++;
		     	   }
		     	   else{
		     		   action[3] = false;
		     		   salto_array[5] = 0;
		     	   }		   
	        }
	        if(estado_mario == 2) {
	        	action[4] = true;
	        }
	        else {
	        	action[4] = false;
	        }
	        action[5] = false;
	    }
	    
       }   
       distancia_final = distancia_recorrida;
       return action;
	}
     
}