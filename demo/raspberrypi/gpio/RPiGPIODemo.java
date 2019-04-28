/**
  * @filename       : RPiGPIODemo.java
  * @date           : June 28 2017
  * @author         : soonuse from Github
  * @description:
  *   This demo is written in Java and tested on Raspberry Pi 3 Model B.
  *
  *   Expected result:
  *   The LED state of pin 25 will be toggled continuously.
  ***********************************************************************
  * This file is a demo of GPIO control on Raspberry Pi.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU Lesser General Public License for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with wiringPi.  If not, see <http://www.gnu.org/licenses/>.
  ***********************************************************************
 */

import jwiringpi.*;

public class RPiGPIODemo {
    public static final int HIGH = 1;
    public static final int LOW = 0;
    public static final int OUTPUT = 1;

    public static void main(String[] args) {

int aufladezeit=Integer.parseInt(args[0]);
int entladezeit=Integer.parseInt(args[1]);
boolean blinken=Boolean.parseBoolean(args[2]);
        JWiringPiController gpio = new JWiringPiController();
        if (gpio.wiringPiSetup() < 0) {
            System.out.println("WiringPi setup error");
            return;
        }

        gpio.pinMode(01, OUTPUT);
if(blinken){
	int zaehler=0;

boolean wirdstaerker=true;        
while(true) {
zaehler++;
System.out.println("Neuer Zkylus, "+zaehler);         
   gpio.digitalWrite(01, HIGH);
if(wirdstaerker ){
aufladezeit++;
}else{
aufladezeit--;
}>
            gpio.delay(aufladezeit);
if(aufladezeit>20){
wirdstaerker=false;
}
if(aufladezeit<3){
wirdstaerker=true;
}
            gpio.digitalWrite(01, LOW);
            gpio.delay(entladezeit);//5ms sind ein guter wert
        }
    
}else{
while(true){
gpio.digitalWrite(01,HIGH);
gpio.delay(aufladezeit);
gpio.digitalWrite(01,LOW);
gpio.delay(entladezeit);//5ms sind ein guter Wert
}
}
}
}
