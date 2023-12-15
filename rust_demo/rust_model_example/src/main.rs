// 使用多文件mod中公开导出的结构体，单行导入多个元素
use foo::{FolderModDirectPubStruct, FolderModPriModPubStruct};
// 多文件mod，由文件夹名称和mod中的定义命名，导出路径上的所有元素都必须pub
use foo::pub_mod_in_folder::InnerStruct;
use prelude::*;
// 单文件mod，文件名即为mod名称
use single_file_mod::OuterStruct;

// 声明所有mod，否则无法使用
mod foo;
mod single_file_mod;
mod pre;

// prelude模式
mod prelude {
    // 行内mod
    pub use pre::PreModPubStruct; // 预导入所需要的包
}

fn main() {
    let _ = OuterStruct {};
    let _ = InnerStruct {};
    let _ = FolderModPriModPubStruct {};
    let _ = FolderModDirectPubStruct {};
    let _ = PreModPubStruct {};
}
