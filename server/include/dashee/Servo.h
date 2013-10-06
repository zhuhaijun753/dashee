/**
 * @file include/dashee/Servo.h
 * @author Shahmir Javaid
 *
 * @section LICENSE
 *
 * This file is licensed under the terms and condition
 * mentioned at http://dashee.co.uk/license. In general
 * you are free to copy, distribute, display, and perform the work, 
 * you are also free to make derivative works, under the following conditions:
 *  - Attribution    — You must give dashee's author credit.
 *  - Non-Commercial - You may not use this dashee for commercial 
 *                     purposes without permission.
 *  - Share Alike    — If you alter, transform, or build upon this work, 
 *                     you may distribute the resulting work only under a licence 
 *                     identical to this one.
 *
 * The terms and conditions are updated at http://dashee.co.uk/license. View the
 * project site for more details
 */

#ifndef DASHEE_SERVO_H_
#define DASHEE_SERVO_H_

#include <fcntl.h>
#include <stdio.h>
#include <math.h>
#include <unistd.h>
#include <string.h>
#include <errno.h>

#include <dashee/Log.h>
#include <dashee/Exception/Servo.h>

namespace dashee
{
    class Servo;
}

/**
 * Servo abstraction.
 *
 * This class defines the implementation required for the Servo classes
 * you should be able to control a servo using some major public functions
 * defined here
 */
class dashee::Servo
{
private:
    
protected:
    
    /** 
     * This struct holds the values structures of the settings that
     * a servo can hold, we also, have to set all values to 0, see
     * Constructor, 
     *
     * @instance fallbacks - The values of target, speed and acceleration
     *                       which are set in fallback mode
     * @instance defaults - The default value of the servo, given the target
     *                      speed and acelleration
     * @instance current - The current values of the servo, given the target
     *                     speed and acelleration, set when fallback is called
     *                     and used for revert to set values back to normal
     * @instance fallbackEnabled - The fallbackEnabled instance of this struct
     *                             represents boolean to represent wheather a specific modules
     *                             fallback mode is enabled or not. 0 here represents disabled, > 0
     *                             represents enabled. The 
     */
    struct
    {
        unsigned short int target;
        unsigned short int speed;
        unsigned short int acceleration;
    } fallbacks, defaults, lastknown, fallbackEnabled;
    
    /**
     * Fallback mode flag.
     *
     * This determines whather the controller is in fallback mode
     */
    bool fallbackmode;

    /** 
     * The channel this Servo class represents
     */
    unsigned short int channel;
    
    // Calculate the Target, turn 0-100 to be from 3968-8000
    void PercentageToTarget(unsigned short int * percentage);
    unsigned short int PercentageToTarget(unsigned short int percentage);

    // Calculate the Target, turn 0-100 to be from 3968-8000
    void TargetToPercentage(unsigned short int * target);
    unsigned short int TargetToPercentage(unsigned short int target);

    // Set the @dev
    explicit Servo(const unsigned short int channel);
    
public:

    // Set the target of a given channel
    virtual void setTarget(unsigned short int target) = 0;
    virtual unsigned short int getTarget() = 0;
    
    // Set the struct defaults.target and call setTarget
    virtual void setTargetDefault(unsigned short int target);
    virtual unsigned short int getTargetDefault();
    
    // Set the struct fallbacks.target
    virtual void setTargetFallback(unsigned short int target);
    virtual unsigned short int getTargetFallback();
    
    // Set the struct fallbacks.target
    virtual void setTargetFallbackEnabled(bool);
    virtual bool getTargetFallbackEnabled();
 
    // Returns the status of fallback
    virtual bool isFallback();

    // Call fallback on this servo
    virtual void fallback();
    
    // revert back to last known values
    virtual void revert();
    
    // Destroy all internals, before cleanup
    virtual ~Servo(){};
};

#endif
