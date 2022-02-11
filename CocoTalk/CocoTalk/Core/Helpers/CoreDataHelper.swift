//
//  CoreDataHelper.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/09.
//

import Foundation
import UIKit
import CoreData

class CoreDataHelper {
    
    var context: NSManagedObjectContext {
        get {
            let appDelegate = UIApplication.shared.delegate as! AppDelegate
            let context = appDelegate.persistentContainer.viewContext
            return context
        }
    }
    var coordinator: NSPersistentStoreCoordinator?
    var container: NSPersistentContainer?
    
    init() {
        print("CoreData helper입니다.")
        insertSingleZedd()
        fetchContact()
    }
    
    func insertSingleZedd() {
        let entity = NSEntityDescription.entity(forEntityName: "CoreDataUser", in: context)
        let zedd = ModelProfile(username: "제드", phone: "01099317685")
        
        if let entity = entity {
            let person = NSManagedObject(entity: entity, insertInto: context)
            person.setValue(zedd.username, forKey: "username")
            person.setValue(zedd.phone, forKey: "phone")
            person.setValue(zedd.profileImageURL, forKey: "profileImageURL")
        }
        
        do {
            try context.save()
        }
        catch {
            print(error.localizedDescription)
        }
    }
    
    func fetchContact() {
        let appDelegate = UIApplication.shared.delegate as! AppDelegate
        let context = appDelegate.persistentContainer.viewContext
        
        do {
            let profiles = try context.fetch(CoreDataUser.fetchRequest()) as! [CoreDataUser]
            profiles.forEach {
                print($0.username)
                print($0.phone)
                print($0.bio)
                print($0.profileImageURL)
            }
            print("[profiles.count] \(profiles.count)")
        } catch {
            print(error.localizedDescription)
        }
    }
}
