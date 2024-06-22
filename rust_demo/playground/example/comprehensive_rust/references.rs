pub mod shared_references {
    pub fn practice() {
        let mut a = 'A';
        let mut b = 'B';
        println!("a: {a}, b: {b}");
        let r = &mut a; // 指向可变变量的不可变指针，指针的地址不可修改，但是指针指向的内存可以修改
        println!("r: {}", r);
        *r = 'C';
        println!("a: {a}, b: {b}");

        let mut r = &mut a; // 指向可变变量的可变指针，指针的地址和指向的内容都可以修改
        *r = 'D';
        r = &mut b;
        println!("a: {a}, r: {r}"); // 部分场景指针可以自动解引用
    }
}
