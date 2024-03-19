pub mod payload {
    enum WebEvent {
        PageLoad,
        KeyPress(char),
        Click { x: i64, y: i64 },
    }

    fn inspect(event: WebEvent) {
        // 表达式会从上到下与模式匹配。没有像 C 或 C++ 中那样的跳转。
        // 匹配表达式拥有一个值。值是 match 分支中被执行的最后一个表达式。
        // 从顶部开始，查找与该值匹配的模式，然后沿箭头运行代码。一旦找到匹配，我们便会停止。
        match event {
            WebEvent::PageLoad => println!("page loaded"),
            WebEvent::KeyPress(c) => println!("pressed '{c}'"),
            WebEvent::Click { x, y } => println!("clicked at ({x}, {y})"),
        }
    }

    pub fn practice() {
        inspect(WebEvent::PageLoad);
        inspect(WebEvent::KeyPress('x'));
        inspect(WebEvent::Click { x: 20, y: 30 });
    }
}

/// see [Type Layout](https://doc.rust-lang.org/reference/type-layout.html)
pub mod layout {
    use std::any::type_name;
    use std::fmt::Debug;
    use std::mem::{align_of, size_of};

    fn dbg_size<T>() {
        // 打印类型T的名称、大小、留白
        println!(
            "{}: size {} bytes, align: {} bytes",
            type_name::<T>(),
            size_of::<T>(),
            align_of::<T>()
        );
    }

    enum Foo {
        A,
        B,
    }

    // C风格的枚举
    #[repr(u32)]
    enum FooLikeC {
        A,
        B = 10000,
        C,
        D = 5,
        E,
        F,
        G = 4,
        // H, // 非法，与D重复
    }

    pub fn practice() {
        dbg_size::<Foo>();
        dbg_size::<bool>();
        dbg_size::<[bool; 10]>();
        dbg_size::<Option<bool>>();
        dbg_size::<&i32>();
        dbg_size::<Option<&i32>>();
        dbg_size::<&str>();
        dbg_size::<&[u8]>();
        dbg_size::<&dyn Debug>();

        println!();
        println!("FooLikeC::A: {}", FooLikeC::A as u32);
        println!("FooLikeC::B: {}", FooLikeC::B as u32);
        println!("FooLikeC::C: {}", FooLikeC::C as u32);
        println!("FooLikeC::D: {}", FooLikeC::D as u32);
        println!("FooLikeC::E: {}", FooLikeC::E as u32);
        println!("FooLikeC::F: {}", FooLikeC::F as u32);
    }
}
