/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package debug;

/**
 *
 * @author arpaf
 */
public interface DebuggableComponent {
	/**
	 * reads state of the desired value and dumps it as a string
	 * @return 
	 */
	public String readState();
}
