package com.driver.repositories;

import com.driver.model.*;
import com.sun.jdi.IntegerValue;
import io.swagger.models.auth.In;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
public class Repository {

    HashMap<String,Airport> airportsMap;
    HashMap<Integer,Flight> flightsMap;
    HashMap<Integer,Passenger> passengerHashMap;
    HashMap<Integer, List<Integer>> bookingHashMap;
    HashMap<Integer,Integer> seatsMap;



    public Repository(){
        airportsMap = new HashMap<>();
        flightsMap = new HashMap<>();
        passengerHashMap = new HashMap<>();
        bookingHashMap = new HashMap<>();
        seatsMap = new HashMap<>();
    }



    public String addAirport(Airport airport){

        //Simply add airport details to your database
        //Return a String message "SUCCESS"
        airportsMap.put(airport.getAirportName(),airport);
        return "SUCCESS";
    }

    public String getLargestAirportName(){

        //Largest airport is in terms of terminals. 3 terminal airport is larger than 2 terminal airport
        //Incase of a tie return the Lexicographically smallest airportName
        List<Airport> airportList = new ArrayList<>();
        for(Airport airport:airportsMap.values()){
            airportList.add(airport);
        }
        if(airportList.size()==0){
            return "";
        }
        String result = airportList.get(0).getAirportName();
        int curr = airportList.get(0).getNoOfTerminals();
        for(Airport airport:airportList){
            if(curr==airport.getNoOfTerminals()){
                if(result.compareTo(airport.getAirportName())>1){
                    result = airport.getAirportName();
                }
            }
            if(curr<airport.getNoOfTerminals()){
                curr=airport.getNoOfTerminals();
                result = airport.getAirportName();
            }
        }
        return result;
    }

    public double getShortestDurationOfPossibleBetweenTwoCities(City fromCity, City toCity){

        //Find the duration by finding the shortest flight that connects these 2 cities directly
        //If there is no direct flight between 2 cities return -1.
        double result = -1;
        for(Flight flight:flightsMap.values()){
            if(fromCity.equals(flight.getFromCity()) && toCity.equals(flight.getToCity())){
                if(result==-1){
                    result = flight.getDuration();
                }
                else{
                    result = Math.min(result,flight.getDuration());
                }
            }
        }
        return result;
    }

    public int getNumberOfPeopleOn(Date date, String airportName){

        //Calculate the total number of people who have flights on that day on a particular airport
        //This includes both the people who have come for a flight and who have landed on an airport after their flight
        Integer curr = 0;
        Airport airport = airportsMap.get(airportName);
        for(Flight flight:flightsMap.values()){
            if(flight.getFlightDate().equals(date) && airport!=null && (airport.getCity().equals(flight.getFromCity()) || airport.getCity().equals(flight.getToCity()))){
                curr += seatsMap.get(flight.getFlightId());
            }
        }
        return curr;
    }

    public String addPassenger(Passenger passenger){

        //Add a passenger to the database
        //And return a "SUCCESS" message if the passenger has been added successfully.
        passengerHashMap.put(passenger.getPassengerId(),passenger);
        return "SUCCESS";
    }


    public int calculateFlightFare(Integer flightId){

        //Calculation of flight prices is a function of number of people who have booked the flight already.
        //Price for any flight will be : 3000 + noOfPeopleWhoHaveAlreadyBooked*50
        //Suppose if 2 people have booked the flight already : the price of flight for the third person will be 3000 + 2*50 = 3100
        //This will not include the current person who is trying to book, he might also be just checking price
        Integer p = seatsMap.get(flightId);
        if(p==null){
            return 3000;
        }
        return 3000+50*seatsMap.get(flightId);

    }


    public String bookATicket(Integer flightId,Integer passengerId){

        //If the numberOfPassengers who have booked the flight is greater than : maxCapacity, in that case :
        //return a String "FAILURE"
        //Also if the passenger has already booked a flight then also return "FAILURE".
        //else if you are able to book a ticket then return "SUCCESS"
        if((flightsMap.containsKey(flightId)==false || passengerHashMap.containsKey(passengerId)==false)||(flightsMap.containsKey(flightId) && seatsMap.containsKey(flightId) && seatsMap.get(flightId)>flightsMap.get(flightId).getMaxCapacity())){
            return "FAILURE";
        }
        if(bookingHashMap.containsKey(passengerId)){
            if(bookingHashMap.get(passengerId).contains(flightId)){
                return "FAILURE";
            }
            bookingHashMap.get(passengerId).add(flightId);
        }
        else{
            List<Integer> list = new ArrayList<>();
            list.add(flightId);
            bookingHashMap.put(passengerId,list);
        }
        if(seatsMap.containsKey(flightId)){
            seatsMap.put(flightId,seatsMap.get(flightId)+1);
        }
        else{
            seatsMap.put(flightId,1);
        }
        return "SUCCESS";
    }
    public int countOfBookingsDoneByPassengerAllCombined(int passengerId){
        if(bookingHashMap.containsKey(passengerId))
        return bookingHashMap.get(passengerId).size();

        return 0;
    }

    public String cancelATicket(Integer flightId,Integer passengerId){

        //If the passenger has not booked a ticket for that flight or the flightId is invalid or in any other failure case
        // then return a "FAILURE" message
        // Otherwise return a "SUCCESS" message
        // and also cancel the ticket that passenger had booked earlier on the given flightId
        if(flightsMap.containsKey(flightId)==false || passengerHashMap.containsKey(passengerId)==false){
            return "FAILURE";
        }
        if(bookingHashMap.containsKey(passengerId)){
            if(bookingHashMap.get(passengerId).contains(flightId)){
                bookingHashMap.get(passengerId).remove(Integer.valueOf(flightId));
                seatsMap.put(passengerId,seatsMap.get(passengerId)-1);
            }
            else{
                return "FAILURE";
            }
        }
        return "SUCCESS";
    }


    public int countOfBookingsDoneByPassengerAllCombined(Integer passengerId){

        //Tell the count of flight bookings done by a passenger: This will tell the total count of flight bookings done by a passenger :
        if(bookingHashMap.get(passengerId)==null){
            return 0;
        }
        return bookingHashMap.get(passengerId).size();
    }

    public String addFlight(Flight flight){

        //Return a "SUCCESS" message string after adding a flight.
        flightsMap.put(flight.getFlightId(),flight);
        return "SUCCESS";
    }


    public String getAirportNameFromFlightId(Integer flightId){

        //We need to get the starting airportName from where the flight will be taking off (Hint think of City variable if that can be of some use)
        //return null incase the flightId is invalid or you are not able to find the airportName
        Flight flight = flightsMap.get(flightId);
        if(flight==null){
            return null;
        }
        for(Airport airport:airportsMap.values()){
            if(airport.getCity().equals(flight.getFromCity())){
                return airport.getAirportName();
            }
        }
        return null;
    }


    public int calculateRevenueOfAFlight(Integer flightId){

        //Calculate the total revenue that a flight could have
        //That is of all the passengers that have booked a flight till now and then calculate the revenue
        //Revenue will also decrease if some passenger cancels the flight
        Integer k = seatsMap.get(flightId);
        if(k==null){
            return 0;
        }
        return (k*3000) + (50*(((k-1)*(k)))/2);
    }

}
