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

package ch.idsia.agents.controllers.human;

import ch.idsia.agents.Agent;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by PLG Group.
 /* User: Moisés Martínez 
 * Date: Jan 24, 2014
 * Package: ch.idsia.controllers.agents.controllers;
 */
public final class T1HumanAgent extends KeyAdapter implements Agent
{
    
    private boolean[] Action    = null;
    private String Name         = "T1HumanAgent";
    
    int tick;
    static BufferedWriter fichero = null;   
	String [] auxString = new String[5];
	
    public T1HumanAgent()
    {
    	escribir();
        this.reset(); 
    }
    public static void escribir()
    {
    	try {
			fichero = new BufferedWriter(new FileWriter("ejemplos_Human.txt", true));
			//fichero.close();
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
    public void integrateObservation(Environment environment)
    {
    	String miString = null;
    	StringBuffer aux_sb = new StringBuffer();
    	StringBuffer sb = new StringBuffer();

        System.out.println("------------------ TICK " + tick + " ------------------");
        
        // Devuelve un array de 19x19 donde Mario ocupa la posicion 9,9 con la union de los dos arrays
        // anteriores, es decir, devuelve en un mismo array la informacion de los elementos de la
        // escena y los enemigos.
        System.out.println("\nMERGE");
        byte [][] env;
        env = environment.getMergedObservationZZ(1, 1); 
        for (int mx = 0; mx < env.length; mx++) {
            //System.out.print(mx + ": [");
            for (int my = 0; my < env[mx].length; my++) {
                //System.out.print(env[mx][my] + " ");
            	miString = sb.append(String.valueOf(env[mx][my])+", ").toString();
            }

            //System.out.println("]");
        }
        
        // Posicion de Mario utilizando las coordenadas del sistema
        System.out.println("POSICION MARIO");
        float[] posMario;
        posMario = environment.getMarioFloatPos();
        for (int mx = 0; mx < posMario.length; mx++) {
            System.out.print(posMario[mx] + " ");
        	//escribir(String.valueOf(posMario[mx]+", "));
        	miString = sb.append(String.valueOf(posMario[mx]+", ")).toString();
        }
        
        // Posicion que ocupa Mario en el array anterior
        System.out.println("\nPOSICION MARIO MATRIZ");
        int[] posMarioEgo;
        posMarioEgo = environment.getMarioEgoPos();
        for (int mx = 0; mx < posMarioEgo.length; mx++) {
        	System.out.print(posMarioEgo[mx] + " ");
        	//escribir(String.valueOf(posMarioEgo[mx]+", "));
        	miString = sb.append(String.valueOf(posMarioEgo[mx]+", ")).toString();
        }
            
        // Estado de mario
        // marioStatus, marioMode, isMarioOnGround (1 o 0), isMarioAbleToJump() (1 o 0), isMarioAbleToShoot (1 o 0), 
        // isMarioCarrying (1 o 0), killsTotal, killsByFire,  killsByStomp, killsByShell, timeLeft
        System.out.println("\nESTADO MARIO");
        int[] marioState;
        marioState = environment.getMarioState();
        for (int mx = 0; mx < marioState.length; mx++) {
        	System.out.print(marioState[mx] + " ");
        	//escribir(String.valueOf(marioState[mx]+", "));
        	miString = sb.append(String.valueOf(marioState[mx]+", ")).toString(); 
        }
             
        

        // Mas informacion de evaluacion...
        // distancePassedCells, distancePassedPhys, flowersDevoured, killsByFire, killsByShell, killsByStomp, killsTotal, marioMode,
        // marioStatus, mushroomsDevoured, coinsGained, timeLeft, timeSpent, hiddenBlocksFound
        System.out.println("\nINFORMACION DE EVALUACION");
        int[] infoEvaluacion;
        infoEvaluacion = environment.getEvaluationInfoAsInts();
        for (int mx = 0; mx < infoEvaluacion.length; mx++){
        	System.out.print(infoEvaluacion[mx] + " ");
        	//escribir(String.valueOf(infoEvaluacion[mx]+", "));
        	miString = sb.append(String.valueOf(infoEvaluacion[mx]+", ")).toString();
        }
        if(tick >= 5) {
    		miString = sb.append(auxString[tick%5]+", ").toString();
        	auxString[tick%5] = aux_sb.append(String.valueOf(infoEvaluacion[10]+", ")).append(String.valueOf(infoEvaluacion[6])).toString();
        }
    	else {
        	auxString[tick] = aux_sb.append(String.valueOf(infoEvaluacion[10]+", ")).append(String.valueOf(infoEvaluacion[6])).toString();
    	}
        // Informacion del refuerzo/puntuacion que ha obtenido Mario. Nos puede servir para determinar lo bien o mal que lo esta haciendo.
        // Por defecto este valor engloba: reward for coins, killed creatures, cleared dead-ends, bypassed gaps, hidden blocks found
        System.out.println("\nREFUERZO");
        int reward = environment.getIntermediateReward();
        System.out.print(reward);
        miString = sb.append(String.valueOf(reward+"\n")).toString();
    	try {
			fichero.write(miString);
			fichero.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	System.out.println("\n");
        tick++;
        miString = null;
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
            case KeyEvent.VK_UP:
                Action[Mario.KEY_UP] = isPressed;
                break;

            case KeyEvent.VK_S:
                Action[Mario.KEY_JUMP] = isPressed;
                break;
            case KeyEvent.VK_A:
                Action[Mario.KEY_SPEED] = isPressed;
                break;
        }
    }

}



