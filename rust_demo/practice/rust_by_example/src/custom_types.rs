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
