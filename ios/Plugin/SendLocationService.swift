//
//  SendLocationService.swift
//  SBackgroundGeolocation
//
//  Created by Jorge Videla on 18-03-22.
//

import Foundation
import CoreLocation

class SendLocationService{
    public static var url = "";
    public static var token = "";
    public static var idRuta = 0;
    
    var location:CLLocation;
    var deviceId:String;
    
    init(location : CLLocation) {
        self.location = location;
        deviceId = "Sin info";
        self.config();
    }
    
    func config(){
        let device = UIDevice.current.identifierForVendor;
        if let d = device {
            deviceId = d.uuidString;
        }
    }
    
    
    func sendLocation(){
        let json:[String:Any] = [
            "lat":self.location.coordinate.latitude,
            "lon":self.location.coordinate.longitude,
            "velocidad": round(self.location.speed),
            "direccion": round(self.location.course),
            "idRuta": SendLocationService.idRuta,
            "time": self.location.timestamp.timeIntervalSince1970*1000
        ]
      
        let jsonData = try? JSONSerialization.data(withJSONObject: json,options: [])
        
        print("Enviando localización...")
        print(jsonData?.debugDescription) 
        
        let url = URL(string:SendLocationService.url)!
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.httpBody = jsonData
        request.setValue("Bearer "+SendLocationService.token, forHTTPHeaderField: "Authorization");
        request.setValue(self.deviceId, forHTTPHeaderField: "device-id");
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        request.addValue("application/json", forHTTPHeaderField: "Accept")
        
        let task = URLSession.shared.dataTask(with: request){data,response,error in
            guard let data = data,error == nil else{
                print(error?.localizedDescription);
                return;
            }
            print("Solicitud enviada a Q con exito");
        }
        
        task.resume()
    }
}
