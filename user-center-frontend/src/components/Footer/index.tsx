import {GithubOutlined} from '@ant-design/icons';
import {DefaultFooter} from '@ant-design/pro-layout';
import {PLANET_LINK} from "@/constants";

const Footer: React.FC = () => {
  const defaultMessage = 'LKJ出品';
  const currentYear = new Date().getFullYear();
  return (
    <DefaultFooter
      copyright={`${currentYear} ${defaultMessage}`}
      links={[
        {
          key: 'planet',
          title: '用户管理系统',
          href: PLANET_LINK,
          blankTarget: true,
        },
        {
          key: 'codeNav',
          title: 'LKJ有限公司',
          href: 'https://github.com/LKJ-np/UserCenter/tree/master',
          blankTarget: true,
        },
        {
          key: 'github',
          title: <><GithubOutlined/> LKJ GitHub</>,
          href: 'https://github.com/LKJ-np/UserCenter/tree/master',
          blankTarget: true,
        },

      ]}
    />
  );
};

export default Footer;
