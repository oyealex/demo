pub mod normal_struct {
    struct Person {
        name: String,
        age: u8,
    }

    pub fn practice() {
        let person = Person { name: String::from("Jack"), age: 20 };
        describe(&person);

        let name = String::from("Alex");
        let age = 23;
        let person = Person { name, age };
        describe(&person);

        let person = Person { name: person.name, ..person };
        describe(&person);
    }

    fn describe(person: &Person) {
        println!("{} is {} years old", person.name, person.age);
    }
}

pub mod zero_sized_struct {
    struct NoBody {}

    pub fn practice() {
        let _no_body = NoBody {};
    }
}

pub mod tuple_struct {
    struct Point(i32, i32);
    
    pub fn practice() {
        
    }
}
