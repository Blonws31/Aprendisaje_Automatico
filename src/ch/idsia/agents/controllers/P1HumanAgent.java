/*
 * Copyright (c) 2012-2013, Moisés Martínez
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
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
//import ch.idsia.agents.controllers.human.*;

/**
 * Created by PLG Group.
 /* User: Moisés Martínez 
 * Date: Jan 24, 2014
 * Package: ch.idsia.controllers.agents.controllers;
 */
public final class P1HumanAgent extends KeyAdapter implements Agent {
    
    private boolean[] Action    = null;
    private String Name         = "P1HumanAgent";
    
    int tick;
	int aux_salto = 0;
	boolean Salto = false;
    static BufferedWriter fichero = null;
	String [] auxString = new String[25];
	String [] coin_bricks = new String[25];
	String [] arrayAuxiliar = new String[2];
	String [] arrayInicial = new String[2];
	String miString = null;
	String escritura_final = null;
	StringBuffer aux_sb = new StringBuffer();
	StringBuffer sb = new StringBuffer();
		
	public P1HumanAgent() {
    	escribir();
        this.reset(); 
    }
    
	public static void escribir()
    {
    	try {
			fichero = new BufferedWriter(new FileWriter("P1HumanAgent.arff", true));
			String sFichero = "P1HumanAgent.arff";
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
			fichero.write("@RELATION P1HumanAgent\n\n");
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
			fichero.write("@ATTRIBUTE mario_x NUMERIC\n");
			fichero.write("@ATTRIBUTE mario_y NUMERIC\n");
			fichero.write("@ATTRIBUTE marioStatus NUMERIC\n");
			fichero.write("@ATTRIBUTE marioMode NUMERIC\n");
			fichero.write("@ATTRIBUTE isMarioOnGround {1, 0}\n");
			fichero.write("@ATTRIBUTE isMarioAbleToJump {1, 0}\n");
			fichero.write("@ATTRIBUTE isMarioAbleToShoot {1, 0}\n");
			fichero.write("@ATTRIBUTE isMarioCarrying {1, 0}\n");
			fichero.write("@ATTRIBUTE killsTotal NUMERIC\n");
			fichero.write("@ATTRIBUTE killsByFire NUMERIC\n");
			fichero.write("@ATTRIBUTE killsByStomp NUMERIC\n");
			fichero.write("@ATTRIBUTE killsByShell NUMERIC\n");
			fichero.write("@ATTRIBUTE timeLeft NUMERIC\n");
			fichero.write("@ATTRIBUTE distancePassedCells NUMERIC\n");
			fichero.write("@ATTRIBUTE distancePassedPhys NUMERIC\n");
			fichero.write("@ATTRIBUTE flowersDevoured NUMERIC\n");
			fichero.write("@ATTRIBUTE mushroomsDevoured NUMERIC\n");
			fichero.write("@ATTRIBUTE coinsGained NUMERIC\n");
			fichero.write("@ATTRIBUTE timeSpent NUMERIC\n");
			fichero.write("@ATTRIBUTE hiddenBlocksFound NUMERIC\n");
			fichero.write("@ATTRIBUTE reward NUMERIC\n");
			fichero.write("@ATTRIBUTE coins NUMERIC\n");
			fichero.write("@ATTRIBUTE bricks NUMERIC\n");
			fichero.write("@ATTRIBUTE enemys NUMERIC\n");
			fichero.write("@ATTRIBUTE coins_6 NUMERIC\n");
			fichero.write("@ATTRIBUTE enemys_6 NUMERIC\n");
			fichero.write("@ATTRIBUTE coins_12 NUMERIC\n");
			fichero.write("@ATTRIBUTE enemys_12 NUMERIC\n");
			fichero.write("@ATTRIBUTE coins_24 NUMERIC\n");
			fichero.write("@ATTRIBUTE enemys_24 NUMERIC\n");
			fichero.write("@ATTRIBUTE Action {PARADO, SALTA, AVANZA, JUMP-ADVANCE}\n");
			fichero.write("@ATTRIBUTE prediccion_24 NUMERIC\n");
			fichero.write("@data\n");
			fichero.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
    }
    
    @Override
    public String getName() { return Name; }

    @Override
    public void setName(String name) { Name = name; }

    @Override
    public boolean[] getAction() { return Action; }

    @Override
    public void integrateObservation(Environment environment){
    	aux_sb = new StringBuffer();
    	sb = new StringBuffer();
        
        // Devuelve un array de 19x19 donde Mario ocupa la posicion 9,9 con la union de los dos arrays
        // anteriores, es decir, devuelve en un mismo array la informacion de los elementos de la
        // escena y los enemigos.
        //System.out.println("\nMERGE:");
        byte [][] env;
        env = environment.getMergedObservationZZ(1, 1);
        int coins = 0;
        int bricks = 0;
        int enemys = 0;
        for (int mx = 0; mx < env.length; mx++) {
            for (int my = 0; my < env[mx].length; my++) {
            	if(env[mx][my] == 2) {
            		coins++;
            	}
            	else if(env[mx][my] == -24) {
            		bricks++;
            	}
            	else if(env[mx][my] == 80) {
            		enemys++;
            	}
            	if(env[mx][my] == 2 || env[mx][my] == 3 || env[mx][my] == 25) {
            		env[mx][my] = 0;
            	}
            }
        }
        miString = sb.append(String.valueOf(env[8][10])+", "+String.valueOf(env[9][10])+", "+String.valueOf(env[9][11])+", "
    			+String.valueOf(env[10][10])+", "+String.valueOf(env[11][10])+", "
    			+String.valueOf(env[10][11])+", "+String.valueOf(env[11][11])+", "
    			+String.valueOf(env[10][12])+", "+String.valueOf(env[11][12])+", ").toString();
    	
     // Posicion de Mario utilizando las coordenadas del sistema
        float[] posMario;
        posMario = environment.getMarioFloatPos();
        for (int mx = 0; mx < posMario.length; mx++) {
            	miString = sb.append(String.valueOf(posMario[mx]+", ")).toString();
        }
        
        // Estado de mario
        // marioStatus, marioMode, isMarioOnGround (1 o 0), isMarioAbleToJump() (1 o 0), isMarioAbleToShoot (1 o 0), 
        // isMarioCarrying (1 o 0), killsTotal, killsByFire,  killsByStomp, killsByShell, timeLeft
        int[] marioState;
        marioState = environment.getMarioState();
        for (int mx = 0; mx < marioState.length; mx++) {
        	miString = sb.append(String.valueOf(marioState[mx]+", ")).toString(); 
        }
             
        // Mas informacion de evaluacion...
        // distancePassedCells, distancePassedPhys, flowersDevoured, killsByFire, killsByShell, killsByStomp, killsTotal, marioMode,
        // marioStatus, mushroomsDevoured, coinsGained, timeLeft, timeSpent, hiddenBlocksFound
        int[] infoEvaluacion;
        infoEvaluacion = environment.getEvaluationInfoAsInts();
        for (int mx = 0; mx < infoEvaluacion.length; mx++){
        	if(mx != 3 && mx != 4 && mx != 5 && mx != 6 && mx != 7 && mx != 8 && mx != 11) {
            	miString = sb.append(String.valueOf(infoEvaluacion[mx]+", ")).toString();
        	}
        }
        miString = sb.append(String.valueOf(infoEvaluacion[0]+", ")).toString();
        // Informacion del refuerzo/puntuacion que ha obtenido Mario. Nos puede servir para determinar lo bien o mal que lo esta haciendo.
        // Por defecto este valor engloba: reward for coins, killed creatures, cleared dead-ends, bypassed gaps, hidden blocks found
        int reward = environment.getIntermediateReward();
        miString = sb.append(String.valueOf(reward+", ")).toString();
        miString = sb.append(String.valueOf(coins+", "+bricks+", "+enemys+", ")).toString(); 
        
        if(tick >= 25) {
        	escritura_final = auxString[tick%25];
        	auxString[tick%25] = miString;
            
            if((tick%25)+6 < 25) {
            	arrayAuxiliar = coin_bricks[((tick%25)+6)].split(", ");
            	arrayInicial = coin_bricks[(tick%25)].split(", ");
            }
            else {
            	arrayAuxiliar = coin_bricks[((tick%25)+6)%25].split(", ");
            	arrayInicial = coin_bricks[(tick%25)].split(", ");
            }
        	escritura_final = sb.append((Integer.parseInt(arrayAuxiliar[0]) - Integer.parseInt(arrayInicial[0]))+", ").toString();
        	escritura_final = sb.append((Integer.parseInt(arrayAuxiliar[1]) -Integer.parseInt(arrayInicial[1]))+", ").toString();
        	
        	if((tick%25)+12 < 25) {
            	arrayAuxiliar = coin_bricks[((tick%25)+6)].split(", ");
            	arrayInicial = coin_bricks[(tick%25)].split(", ");
            }
            else {
            	arrayAuxiliar = coin_bricks[((tick%25)+12)%25].split(", ");
            	arrayInicial = coin_bricks[(tick%25)].split(", ");
            }
        	escritura_final = sb.append((Integer.parseInt(arrayAuxiliar[0]) -Integer.parseInt(arrayInicial[0]))+", ").toString();
        	escritura_final = sb.append((Integer.parseInt(arrayAuxiliar[1]) -Integer.parseInt(arrayInicial[1]))+", ").toString();
        	
        	if((tick%25)+24 < 25) {
            	arrayAuxiliar = coin_bricks[((tick%25)+24)].split(", ");
            	arrayInicial = coin_bricks[(tick%25)].split(", ");
            }
            else {
            	arrayAuxiliar = coin_bricks[((tick%25)+24)%25].split(", ");
            	arrayInicial = coin_bricks[(tick%25)].split(", ");
            }
        	escritura_final = sb.append((Integer.parseInt(arrayAuxiliar[0]) -Integer.parseInt(arrayInicial[0]))+", ").toString();
        	escritura_final = sb.append((Integer.parseInt(arrayAuxiliar[1]) -Integer.parseInt(arrayInicial[0]))+", ").toString();
        	            
            coin_bricks[tick%25] = aux_sb.append(String.valueOf(infoEvaluacion[10]+", ")).append(String.valueOf(infoEvaluacion[6])).toString();

            getAction();
         
            //Action PARADO
            if(Action[0] == false && Action[1] == false && (Action[2] == false || Action[2] == true) && Action[3] == false && (Action[4] == false || 
       				Action[4] == true ) && (Action[5] == false || Action[5] == true)) {
            	escritura_final = sb.append("PARADO, "+prediccion_coins_24(marioState, infoEvaluacion, coins, bricks, "PARADO")+"\n").toString();  
            	//escritura_final = sb.append("PARADO\n").toString();   
       		}
    	   	//Action Salta
            else if(Action[0] == false && Action[1] == false && (Action[2] == false || Action[2] == true)  && Action[3] == true && (Action[4] == false || 
    	   			Action[4] == true) && (Action[5] == false || Action[5] == true)) {
            	escritura_final = sb.append("PARADO, "+prediccion_coins_24(marioState, infoEvaluacion, coins, bricks, "SALTA")+"\n").toString(); 
            	//escritura_final = sb.append("SALTA\n").toString();   
    	   	}
    	   	//Action Avanza
            else if((Action[0] == true || Action[1] == true) && (Action[2] == false || Action[2] == true)  && Action[3] == false && (Action[4] == false || 
    	   			Action[4] == true) && (Action[5] == false || Action[5] == true)) {
            	escritura_final = sb.append("PARADO, "+prediccion_coins_24(marioState, infoEvaluacion, coins, bricks, "AVANZA")+"\n").toString(); 
            	//escritura_final = sb.append("AVANZA\n").toString();   
    	   	}
    	   	//Action Salta + Avanza
            else if((Action[0] == true || Action[1] == true) && (Action[2] == false || Action[2] == true)  && Action[3] == true && (Action[4] == false || 
    	   			Action[4] == true) && (Action[5] == false || Action[5] == true)) {
    	   		escritura_final = sb.append("JUMP-ADVANCE, "+prediccion_coins_24(marioState, infoEvaluacion, coins, bricks, "JUMP-ADVANCE")+"\n").toString(); 
            	//escritura_final = sb.append("JUMP-ADVANCE\n").toString();  
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
    		coin_bricks[tick] = aux_sb.append(String.valueOf(infoEvaluacion[10]+", ")).append(String.valueOf(infoEvaluacion[6])).toString();
    	}
        tick++;
        miString = null;
        escritura_final = null;
        aux_sb = null;
    }

    @Override
    public void giveIntermediateReward(float intermediateReward)
    {
    }

    @Override
    public void reset()
    {
        Action = new boolean[Environment.numberOfKeys];
    }

    @Override
    public void setObservationDetails(final int rfWidth, final int rfHeight, final int egoRow, final int egoCol)
    {
    }

    public boolean[] getAction(Environment observation)
    {	
        return Action;
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        toggleKey(e.getKeyCode(), true);
    }

    @Override
    public void keyReleased(KeyEvent e)
    {		
        toggleKey(e.getKeyCode(), false);
    }

    private void toggleKey(int keyCode, boolean isPressed)
    {
        switch (keyCode)
        {
            case KeyEvent.VK_LEFT:
                Action[Mario.KEY_LEFT] = isPressed;
                break;
            case KeyEvent.VK_RIGHT:
                Action[Mario.KEY_RIGHT] = isPressed;
                break;
            case KeyEvent.VK_DOWN:
                Action[Mario.KEY_DOWN] = isPressed;
                break;
            case KeyEvent.VK_S:
                Action[Mario.KEY_JUMP] = isPressed;
                break;
            case KeyEvent.VK_A:
                Action[Mario.KEY_SPEED] = isPressed;
                break;
            case KeyEvent.VK_UP:
                Action[Mario.KEY_UP] = isPressed;
                break;	
        }
    }

    public int prediccion_coins_24(int[] marioState, int[] infoEvaluacion, int coins, int bricks,  String Action) {
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
    			if(Action.equals("AVANZA")) {
    				prediccion = 0;
    			}
    			else if(Action.equals("JUMP-ADVANCE")) {
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
    			if(Action.equals("AVANZA")) {
    				if(marioState[2] == 1) {
    					prediccion = 0;
    				}
    				else {
    					prediccion = 4;
    				}
    			}
    			else if(Action.equals("JUMP-ADVANCE")) {
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
    				if(Action.equals("JUMP-ADVANCE")) {
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
    		if(Action.equals("JUMP-ADVANCE")) {
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
    			if(Action.equals("PARADO") || Action.equals("SALTA")) {
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
    			if(Action.equals("AVANZA")) {
    				prediccion = 6;
    			}
    			else {
    				prediccion = 0;
    			}
    		}
    		else if(coins == 6) {
    			if(Action.equals("AVANZA")) {
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
    				if(Action.equals("JUMP-ADVANCE")) {
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
    			if(Action.equals("PARADO") || Action.equals("SALTA")) {
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
    		if(Action.equals("PARADO")) {
    			if(marioState[2] == 1) {
    				prediccion = 7;
    			}
    			else {
    				prediccion = 8;
    			}
    		}
    		else if(Action.equals("SALTA")) {
    			prediccion = 8;
    		}
    		else if(Action.equals("AVANZA")){
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
    		if(Action.equals("AVANZA")) {
    			prediccion = 1;
    		}
    		else if(Action.equals("JUMP-ADVANCE")) {
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
    			if(Action.equals("PARADO")) {
    				prediccion = 1;
    			}
    			else if(Action.equals("SALTA")) {
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
    		if(Action.equals("SALTA")) {
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
    			if(Action.equals("SALTA") || Action.equals("PARADO")) {
    				prediccion = 1;
    			}
    			else if(Action.equals("AVANZA")) {
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



