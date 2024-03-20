#[allow(dead_code)]
pub mod comments {
    pub fn run() {
        // 单行注释

        /* 块注释 */

        /* 块注释可以 /*嵌套*/ /*/*/*很多层*/*/*/*/

        /*
         * 块注释中的星号是可选的
         */

        let x = 5 + /* 90 + */ 5;
        println!("x is {x}");
    }
}

#[allow(dead_code)]
/// 文档注释
/// `cargo doc`可以在`target/doc`中生成文档。
pub mod documentation {
    /// 人物信息
    struct Person {
        /// 姓名
        name: String,
    }

    impl Person {
        /// 构造一个新的人物信息对象。
        ///
        /// # Arguments
        ///
        /// * `name`: 人物名称。
        ///
        /// returns: 新的人物对象。
        ///
        /// # Examples
        ///
        /// ```
        /// // 这里可以填入示例代码，示例代码可以通过 cargo test --doc 运行。
        /// use documentation::Person;
        /// let person = Person::new("name");
        /// ```
        pub fn new(name: &str) -> Person {
            Person {
                name: name.to_string(),
            }
        }

        pub fn greet(&self) {
            println!("Hello, {}", self.name);
        }
    }

    pub fn run() {
        let person = Person::new("Jack");
        person.greet();
    }
}

/// 格式化打印
pub mod formatted_print {
    pub fn run() {
        println!("==<占位符>==");
        println!("普通占位符打印 {{}}：一天有{}小时，一周有{}天。", 24, 7);
        println!(
            "占位符中加入数字索引可以按顺序引用后续的参数 {{0}}：{0}不是{1}，{1}不是{0}。",
            "十四", "四十"
        );
        println!("占位符支持命名参数 {{hours_of_day}}：一天有{hours_of_day}小时，一周有{days_of_week}天。", days_of_week = 7, hours_of_day = 24);

        let days_of_week = 7;
        let hours_of_day = 24;
        println!("占位符支持直接引用变量 {{days_of_week}}：一天有{hours_of_day}小时，一周有{days_of_week}天。");
        println!("要打印占位符自身则使用对应的花括号自身转义：{{{{={{，}}}}=}}，{{{{}}}}={{}}，{{{{{{{{}}}}={{{{}}。");
        println!();

        println!("==<数字进制>==");
        println!("数字格式化，十进制{{}}：{}。", 69420);
        println!("数字格式化，二进制{{:b}}： {:b}。", 69420);
        println!("数字格式化，八进制{{:o}}： {:o}。", 69420);
        println!("数字格式化，十六进制小写{{:x}}： {:x}。", 69420);
        println!("数字格式化，十六进制大写{{:X}}： {:X}。", 69420);
        println!();

        println!("==<对齐>==");
        println!("右对齐，左侧添加空格 {{:>5}}：{:>5}。", 1);
        println!("居中对齐，两侧添加空格 {{:^5}}：{:^5}。", 1);
        println!("左对齐，右侧添加空格 {{:<5}}：{:<5}。", 1);
        println!("右对齐，左侧添加数字0 {{:0>5}}：{:0>5}。", 1);
        println!("居中对齐，两侧添加数字0 {{:0^5}}：{:0^5}。", 1);
        println!("左对齐，右侧添加数字0 {{:0<5}}：{:0<5}。", 1);
        println!(
            "实际上，可以使用大部分单个字符来作为对齐填充 {{:_<5}} {{:*>5}}：{:_<5}，{:*>5}。",
            1, 1
        );
        println!(
            "甚至很多奇怪字符 {{:好<5}} {{:👌>5}} {{:{{^5}}：{:好<5}，{:👌>5}，{:{^5}。",
            1, 1, 1
        );
        let width = 5;
        println!("对齐填充的长度可以由变量或命名参数指定 {{number:_>width$}} {{number:#<width2$}}：{number:_>width$}，{number:#<width2$}。", number = 1, width2 = 6);

        let s = "123";
        println!("{:p}", s);
    }
}
