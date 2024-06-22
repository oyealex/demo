pub mod structures {
    use std::mem;

    #[derive(Debug)]
    struct Person {
        name: String,
        age: u8,
    }

    #[derive(Debug)]
    struct Unit;

    #[derive(Debug)]
    struct Pair(i32, f64);

    #[derive(Debug)]
    struct Point {
        x: i32,
        y: i32,
    }

    #[derive(Debug)]
    struct Rectangle {
        top_left: Point,
        bottom_right: Point,
    }

    pub fn run() {
        let name = String::from("Jack");
        let age = 10;
        let jack = Person { name, age };

        println!("jack: {jack:#?}");

        let top_left = Point { x: 10, y: 20 };
        println!("point: {:?}", top_left);

        let bottom_right = Point { x: 30, ..top_left };
        println!("point: {bottom_right:?}");

        let Point {
            x: left_edge,
            y: top_edge,
        } = top_left;
        println!("{left_edge}, {top_edge}");

        let rectangle = Rectangle {
            top_left: Point {
                x: left_edge,
                y: top_edge,
            },
            bottom_right,
        };
        println!("rectangle: {rectangle:?}");

        let unit = Unit;
        println!("unit instance: {unit:?}");

        let pair = Pair(1, 2f64);
        println!("pair contains {:?} and {:?}", pair.0, pair.1);

        println!();
        println!("Unit type size: {}", mem::size_of::<Unit>());
        println!("Pair type size: {}", mem::size_of::<Pair>());
        println!("Point type size: {}", mem::size_of::<Point>());
        println!("Person type size: {}", mem::size_of::<Person>());
        println!("Rectangle type size: {}", mem::size_of::<Rectangle>());
    }
}

pub mod enums {
    use std::mem::size_of_val;

    #[derive(Debug)]
    enum WebEvent {
        PageLoad,
        PageUnload,
        KeyPress(char),
        Paste(String),
        Click { x: i64, y: i64 },
    }

    // 使用type关键字给类型起别名
    type Event = WebEvent;

    impl Event {
        fn print_size(&self) {
            // 在为自身实现方法时可以使用Self关键字代替类型名称
            match self {
                Self::PageLoad => println!("PageLoad size is: {}", size_of_val(self)),
                Self::PageUnload => println!("PageUnload size is: {}", size_of_val(self)),
                Self::KeyPress(_) => println!("KeyPress size is: {}", size_of_val(self)),
                Self::Paste(_) => println!("Paste size is: {}", size_of_val(self)),
                Self::Click { .. } => println!("Click size is: {}", size_of_val(self)),
            }
        }
    }

    fn inspect(event: WebEvent) {
        event.print_size();
        match event {
            WebEvent::PageLoad => println!("page loaded"),
            WebEvent::PageUnload => println!("page unloaded"),
            WebEvent::KeyPress(c) => println!("parsed '{c}'"),
            WebEvent::Paste(s) => println!("pasted \"{s}\""),
            WebEvent::Click { x, y } => println!("clicked at ({x}, {y})"),
        }
    }

    pub fn run() {
        inspect(WebEvent::PageLoad);
        inspect(WebEvent::PageUnload);
        inspect(Event::KeyPress('a'));
        inspect(Event::Paste(String::from("msg")));
        inspect(Event::Click { x: 10, y: 20 });
    }
}

pub mod use_enums {
    enum Status {
        Rich,
        Poor,
    }

    enum Work {
        Civilian,
        Soldier,
    }

    enum State {
        Enable,
        Disable,
    }

    pub fn run() {
        use crate::_3_custom_types::use_enums::State::Enable as Fine;
        use crate::_3_custom_types::use_enums::Status::{Poor, Rich};
        use crate::_3_custom_types::use_enums::Work::*;

        let status = Poor;
        let work = Civilian;
        let state = Fine;

        match status {
            Rich => println!("The rich have lots of money"),
            Poor => println!("The poor have no money..."),
        }

        match work {
            Civilian => println!("Civilians work"),
            Soldier => println!("Soldiers fight"),
        }

        match state {
            Fine => println!("state is fine"),
            State::Disable => println!("state is disabled"),
        }
    }
}

pub mod c_like_enums {
    enum Number {
        Zero,
        One,
        Two,
        Ten = 10,
    }

    enum Color {
        Red = 0xff0000,
        Green = 0x00ff00,
        Blue = 0x0000ff,
    }

    pub fn run() {
        println!("zero is {}", Number::Zero as i32);
        println!("one is {}", Number::One as i32);
        println!("ten is {}", Number::Ten as i32);
        println!("Number has size: {}", std::mem::size_of::<Number>());

        println!();
        println!("roses are #{:06x}", Color::Red as i32);
        println!("violets are #{:06x}", Color::Blue as i32);
        println!("Color has size: {}", std::mem::size_of::<Color>());
    }
}

pub mod list_enum {
    use std::fmt::Formatter;

    use crate::_3_custom_types::list_enum::List::Nil;

    #[derive(Debug)]
    enum List {
        Cons { value: u32, next: Box<List> },
        Nil,
    }

    impl std::fmt::Display for List {
        fn fmt(&self, f: &mut Formatter<'_>) -> std::fmt::Result {
            write!(f, "[")?;
            let mut lst = self;
            loop {
                write!(f, "{}", lst.stringify_self())?;
                match lst {
                    List::Cons { value: _, next } => {
                        lst = &next;
                    }
                    Nil => {
                        break;
                    }
                };
            }
            write!(f, "]")
        }
    }

    impl List {
        fn new() -> List {
            Nil
        }

        fn prepend(self, value: u32) -> List {
            Self::Cons {
                value,
                next: Box::new(self),
            }
        }

        fn len(&self) -> u32 {
            match self {
                List::Cons { value: _, next } => next.len() + 1,
                Nil => 0,
            }
        }

        fn stringify_self(&self) -> String {
            match self {
                List::Cons { value, next: _ } => format!("{value}, "),
                Nil => String::new(),
            }
        }

        fn stringify(&self) -> String {
            match self {
                List::Cons { value, next } => format!("{}, {}", value, next.stringify()),
                Nil => String::from("Nil"),
            }
        }
    }

    pub fn run() {
        let lst = Nil;
        println!(
            "list has length: {}, {lst}, stringify: {}",
            lst.len(),
            lst.stringify()
        );

        let lst = lst.prepend(1);
        println!(
            "list has length: {}, {lst}, stringify: {}",
            lst.len(),
            lst.stringify()
        );

        let lst = lst.prepend(2);
        println!(
            "list has length: {}, {lst}, stringify: {}",
            lst.len(),
            lst.stringify()
        );

        let lst = lst.prepend(3);
        println!(
            "list has length: {}, {lst}, stringify: {}",
            lst.len(),
            lst.stringify()
        );
    }
}

pub mod constants {
    static LANGUAGE: &str = "Rust";
    const THRESHOLD: i32 = 10;

    pub fn run() {
        println!("Const address: {:p}", &THRESHOLD);
    }
}
