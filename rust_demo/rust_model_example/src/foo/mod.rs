// 如果不导出为pub，pub_mod_in_folder.rs中的pub元素也无法在mod外使用
pub mod pub_mod_in_folder;
// 私有导出，外部无法直接使用pri_mod_in_folder.rs中的元素，哪怕是pub
mod pri_mod_in_folder;

// mod主动导出私有文件中的公有元素，也可用于缩短导出路径
pub use foo::pri_mod_in_folder::FolderModPriModPubStruct;

pub struct FolderModDirectPubStruct {}
